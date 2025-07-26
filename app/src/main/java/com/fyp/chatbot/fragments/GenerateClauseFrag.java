package com.fyp.chatbot.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.FragmentGenerateClauseBinding;
import com.fyp.chatbot.viewModels.ClauseViewModel;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GenerateClauseFrag extends Fragment {

    FragmentGenerateClauseBinding binding;
    ClauseViewModel clauseMVVM;
    String clauseTxt,clauseType;
    public GenerateClauseFrag() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGenerateClauseBinding.inflate(getLayoutInflater());
        clauseMVVM = new ViewModelProvider(this).get(ClauseViewModel.class);

        binding.clauseCopyBtn.setVisibility(GONE);
        selectClauseType();
        initListners();
        initObservers();

        return binding.getRoot();
    }

    private void initObservers() {
        clauseMVVM.getGenerateClause().observe(getViewLifecycleOwner(),
                onResult -> {
                    if (onResult != null){
                        clauseTxt = onResult;
                        Log.d("MvvM", "initListners: result is true " + onResult);
                        binding.generateClauseResponse.setText(onResult);
                        binding.clauseCopyBtn.setVisibility(VISIBLE);
                        binding.saveClauseBtn.setVisibility(VISIBLE);
                    }else {
                        Log.d("MvvM", "initListners: result is false" + onResult);
                    }
                });
    }


    private void renderDynamicInputFields(String clauseTypes) {
        binding.dynamicInputFields.removeAllViews();
        switch (clauseTypes){
            case "Confidentiality Clause":
                addAutoCompleteField("Parties Involved", Arrays.asList(
                        "Client and Contractor", "Employer and Employee", "Disclosing and Receiving Parties"
                ));
                addAutoCompleteField("Duration of Confidentiality", Arrays.asList(
                        "1 year", "2 years", "5 years", "Until agreement termination"
                ));
                addAutoCompleteField("Scope of Confidential Info", Arrays.asList(
                        "Trade secrets", "Business plans", "Financial information", "Customer lists"
                ));
                break;

            case "Termination Clause":
                addAutoCompleteField("Parties Involved", Arrays.asList(
                        "Client and Contractor", "Employer and Employee", "Company and Vendor"
                ));
                addAutoCompleteField("Grounds for Termination", Arrays.asList(
                        "Breach of contract", "Non-performance", "Mutual agreement", "Force majeure"
                ));
                addAutoCompleteField("Notice Period", Arrays.asList(
                        "7 days", "15 days", "30 days", "60 days"
                ));
                addAutoCompleteField("Consequences of Termination", Arrays.asList(
                        "No further obligations", "Payment for work completed", "Return of confidential information"
                ));
                break;

            case "Governing Law Clause":
                addAutoCompleteField("Jurisdiction", Arrays.asList(
                        "United States", "United Kingdom", "European Union", "Pakistan", "India", "Australia"
                ));
                addAutoCompleteField("Applicable Law", Arrays.asList(
                        "Contract Law", "Intellectual Property Law", "Commercial Law", "Employment Law"
                ));
                addAutoCompleteField("Dispute Resolution Location", Arrays.asList(
                        "New York", "London", "Islamabad", "Delhi", "Sydney", "Remote Arbitration"
                ));
                break;

            case "Payment Terms Clause":
                addAutoCompleteField("Payment Amount", Arrays.asList(
                        "$1000", "$5000", "$10,000"
                ));
                addAutoCompleteField("Payment Schedule", Arrays.asList(
                        "Monthly", "Quarterly", "Upon completion", "50% upfront, 50% on delivery"
                ));
                addAutoCompleteField("Late Payment Penalties", Arrays.asList(
                        "5% per week", "10% flat fee", "Service suspension", "No penalty"
                ));
                break;

            default:
                break;
        }

    }
    private void addAutoCompleteField(String hint,@Nullable List<String> suggestionsList) {
        // Label TextView
        TextView label = new TextView(this.getContext());
        label.setText(hint);
        label.setTypeface(Typeface.DEFAULT_BOLD);
        label.setTextSize(16);
        label.setTextColor(Color.BLACK);
        label.setPadding(0, 8, 0, 8);
        binding.dynamicInputFields.addView(label);

        AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(this.getContext());
        autoCompleteTextView.setHint(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,4,0,4);
        autoCompleteTextView.setLayoutParams(params);
        autoCompleteTextView.setBackgroundResource(R.drawable.edittext_background);
        autoCompleteTextView.setPadding(16,16,16,16);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(),
                android.R.layout.simple_spinner_dropdown_item,suggestionsList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(0);
        autoCompleteTextView.setOnFocusChangeListener((view , hasFocus)->{
            if (hasFocus){
                autoCompleteTextView.showDropDown();
            }
        });
        autoCompleteTextView.setOnClickListener(view ->
                autoCompleteTextView.showDropDown());
        binding.dynamicInputFields.addView(autoCompleteTextView);
    }
    private void initListners() {
        binding.clauseCopyBtn.setEnabled(true);
        binding.clauseCopyBtn.setOnClickListener(view -> {
            if (clauseTxt != null){
                ClipboardManager clipboardManager = (ClipboardManager)getActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData data = ClipData.newPlainText("Clause",clauseTxt);
                Log.d("ClipData", "initListners: ClipBoard after cliking copy btn:  " + data);
                clipboardManager.setPrimaryClip(data);
                binding.clauseCopyBtn.setEnabled(false);
                Toast.makeText(requireContext(), "Clause copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Nothing to copy", Toast.LENGTH_SHORT).show();
            }
        });
        binding.saveClauseBtn.setOnClickListener(view -> {
            clauseMVVM.saveClause(clauseType,clauseTxt).observe(getViewLifecycleOwner(),
                    onClauseSave -> {
            if (onClauseSave)
                Toast.makeText(this.getContext(), "Clause Saved to list...", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this.getContext(), "Failed to Saved to list...", Toast.LENGTH_SHORT).show();
            });
        });
        binding.generateBtn.setOnClickListener(view -> {

            InputMethodManager im = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentView = requireActivity().getCurrentFocus();
            if (currentView != null)
                im.hideSoftInputFromWindow(view.getWindowToken(),0);

            Map<String,String> inputsValues = new LinkedHashMap<>();
            String clauseType = binding.selectTypeSpinner.getSelectedItem().toString();
            if (!clauseType.isEmpty()){
                for (int i = 0; i < binding.dynamicInputFields.getChildCount() ; i++){
                    View child = binding.dynamicInputFields.getChildAt(i);

                    if(child instanceof EditText){
                        String labelOfInput = ((EditText) child).getHint().toString().trim();
                        String currentInput = ((EditText) child).getText().toString().trim();
                        if (currentInput.isEmpty()){
                            Toast.makeText(this.getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        inputsValues.put(labelOfInput,currentInput);

                    }
                }
            }

            clauseMVVM.setClause(clauseType,inputsValues);

       });
    }
    private void selectClauseType() {
        List<String> clauseTypes = Arrays.asList(
                "Confidentiality Clause",
                "Termination Clause",
                "Governing Law Clause",
                "Payment Terms Clause"
        );
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_spinner_dropdown_item,clauseTypes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.selectTypeSpinner.setAdapter(arrayAdapter);

        binding.selectTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                clauseType = clauseTypes.get(position);
                renderDynamicInputFields(clauseType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
}