package com.fyp.chatbot.repository;

import static androidx.core.content.ContentProviderCompat.requireContext;

import static com.fyp.chatbot.ChatBot.API_KEY;
import static java.security.AccessController.getContext;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;

import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.GeminiApi;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;

public class DocumentRepository {


    public String extractText(Context context,Uri pdfUri) throws IOException{

        try (InputStream inputStream = context.getContentResolver().openInputStream(pdfUri)) {
            String mimeType = context.getContentResolver().getType(pdfUri);

            if ("application/pdf".equals(mimeType)) {
                return extractPdfText(inputStream);
            } else if ("text/plain".equals(mimeType)) {
                return extractPlainText(inputStream);
            }
            throw new IOException("Unsupported file type");
        }
    }
    private String extractPlainText(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.joining("\n"));
    }
    private String extractPdfText(InputStream inputStream) throws IOException {
        PDDocument document = PDDocument.load(inputStream);
        if (document.isEncrypted()){
//        binding.summarizeText.setText("PDF is password-protected");
            document.close();
            throw new IOException("Password-protected PDFs not supported");
        }

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(0);
        stripper.setEndPage(Math.min(10, document.getNumberOfPages())); // Limit pages
        return stripper.getText(document);
    }
    public void sendToGemini(RequestBodyGemini requestBodyGemini, Callback<GeminiResponse> callback) {
        GeminiApi geminiApi = new RetrofitClient("https://generativelanguage.googleapis.com/")
                .getRetrofit().create(GeminiApi.class);

        Call<GeminiResponse> call = geminiApi.generateContent(API_KEY, requestBodyGemini);
        call.enqueue(callback);
    }

}
