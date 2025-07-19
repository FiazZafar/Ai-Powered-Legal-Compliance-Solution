package com.fyp.chatbot.fragments;


import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.fyp.chatbot.databinding.FragmentSummarizationBinding;
import com.fyp.chatbot.repository.DocumentRepository;
import com.fyp.chatbot.repository.GeminiRepository;
import com.fyp.chatbot.viewModels.SummarizationViewModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executors;
import io.noties.markwon.Markwon;

public class SummarizationFragment extends Fragment {
    private SummarizationViewModel viewModel;
    private FragmentSummarizationBinding binding;
    private static final int PICK_PDF_REQUEST = 101;
    private String taskType;
    public SummarizationFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSummarizationBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SummarizationViewModel.class);


        initObserver();

        if (getArguments() != null) {
            taskType = getArguments().getString("TaskType");
        }

        if (taskType != null && !taskType.isEmpty()){
            openFilePicker();
        }else {
            Toast.makeText(this.getContext(), "Empty operation", Toast.LENGTH_SHORT).show();
        }

        binding.uploadAgainBtn.setOnClickListener(view -> openFilePicker());

        return binding.getRoot();
    }

    private void initObserver() {
        viewModel.getAiResult().observe(getViewLifecycleOwner(), onResponse -> {
            binding.linearlayout2.setVisibility(View.GONE);
            if (onResponse != null) {
                displayAnalysisResults(onResponse);
            } else {
                Toast.makeText(requireContext(), "Empty response", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/pdf", "text/plain"});
        startActivityForResult(Intent.createChooser(intent, "Select Document"), PICK_PDF_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri pdfUri = data.getData();
            if (pdfUri != null) {
                handleSelectedDocument(pdfUri);
            }
        }
    }
    private void handleSelectedDocument(Uri docUri) {
        binding.linearlayout2.setVisibility(View.VISIBLE);
        DocumentRepository repository = new DocumentRepository();
        
        Executors.newFixedThreadPool(2).execute(() -> {
            saveDocToInternalStorage(docUri);
            try {
                String extractedText = repository.extractText(requireContext(), docUri);
                    requireActivity().runOnUiThread(() -> {
                        viewModel.setAiResponse(extractedText, taskType);
                    });

            } catch (IOException e) {
                requireActivity().runOnUiThread(() -> showError("File reading failed: "
                        + e.getMessage()));
            }
        });
    }

    private void saveDocToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = this.getContext().getContentResolver()
                    .openInputStream(uri);
            File docFile = new File(this.getContext().getFilesDir(),"Smart_Goval_"
                    + taskType + "_"
                    + System.currentTimeMillis() + ".pdf");
            OutputStream outputStream = new FileOutputStream(docFile);
            byte [] buffer = new byte[1024];
            int length;
            while ( (length = inputStream.read(buffer)) > 0){
                outputStream.write(buffer,0,length);
            }
            inputStream.close();
            outputStream.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void displayAnalysisResults(String markdown) {
        binding.summarizeText.setVisibility(View.VISIBLE);
        binding.linearlayout1.setVisibility(View.VISIBLE);
        binding.linearlayout2.setVisibility(View.GONE);

        Markwon markwon = Markwon.create(requireContext());
        markwon.setMarkdown(binding.summarizeText, markdown);
    }

    private void showError(String message) {
        binding.summarizeText.setVisibility(View.VISIBLE);
        binding.summarizeText.setTextColor(Color.RED);
        binding.summarizeText.setText(message);
    }

}
