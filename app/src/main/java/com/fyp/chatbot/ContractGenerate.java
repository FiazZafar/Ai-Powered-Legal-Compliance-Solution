package com.fyp.chatbot;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.fyp.chatbot.ChatBot.API_KEY;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.databinding.ActivityContractGenerateBinding;
import com.fyp.chatbot.fragments.PreviewContractFragment;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.GeminiApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractGenerate extends AppCompatActivity {

    private ActivityContractGenerateBinding binding;

    //Step 1 Creds
    private String party1Name,party2Name,party1Email,party2Email;
    //Step 2 Creds
    private String contractTitle,contractType,entityTypeA,entityTypeB,startDate,endDate,paymentTerms,jurisdication;
    //Step 3 Creds
    private String generalTerms,specialClauses;
    private Calendar calendar;
    private int progressCounter;
    private ObjectAnimator animation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContractGenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressCounter = 0;
        binding.stepProgress.setProgress(progressCounter);
        calendar = Calendar.getInstance();




        binding.step2Layout.startDate.setOnClickListener(view -> {

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);


            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        startDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        binding.step2Layout.startDate.setText(startDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
        binding.step2Layout.endDate.setOnClickListener(view -> {
            int endDay = calendar.get(Calendar.DAY_OF_MONTH);
            int endMonth = calendar.get(Calendar.MONTH);
            int endYear = calendar.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            endDate = endDay + "/" + endMonth + "/" + endYear;
                            binding.step2Layout.endDate.setText(endDate);
                        }
                    },endYear,endMonth,endDay);
            datePicker.show();
        });

        binding.step1Layout.nextBtnStep1.setOnClickListener(view -> getStep1Data());
        binding.step2Layout.nextBtnStep2.setOnClickListener(view -> getStep2Data());
        binding.step3Layout.nextBtnStep3.setOnClickListener(view -> {
            getStep3Data();
            getStep4Data();
        });
        binding.step4Layout.generateButton.setOnClickListener(view -> generateContract());


        binding.step3Layout.prevBtnStep3.setOnClickListener(view -> {
            progressCounter = progressCounter - 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600); // 600ms nice smooth animation
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            binding.viewFlipper.showPrevious();
        });
        binding.step2Layout.prevBtnStep2.setOnClickListener(view -> {

            progressCounter = progressCounter - 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600); // 600ms nice smooth animation
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            binding.viewFlipper.showPrevious();
        });
        binding.step4Layout.editButton.setOnClickListener(view -> {
            progressCounter = progressCounter - 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600); // 600ms nice smooth animation
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            binding.viewFlipper.showPrevious();
        });

//         Spinner Setup
// In your Activity/Fragment
        List<String> contractTypes = Arrays.asList(
                "Freelance Agreement",
                "NDA (Non-Disclosure Agreement)",
                "Employment Contract",
                "Rental Agreement",
                "Partnership Agreement",
                "Service Agreement",  // Added (common use case)
                "Loan Agreement",     // Added
                "Custom"
        );
        ArrayAdapter<String> contractAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, contractTypes);
        binding.step2Layout.contractTypeSpinner.setAdapter(contractAdapter);

        List<String> entityTypes = Arrays.asList(
                "Individual",
                "Sole Proprietorship",
                "LLC (Limited Liability Company)",
                "Corporation",
                "Partnership",
                "Non-Profit",
                "Government Entity"
        );

        ArrayAdapter<String> entityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,entityTypes);
        binding.step2Layout.entityTypeA.setAdapter(entityAdapter);
        binding.step2Layout.entityTypeB.setAdapter(entityAdapter);
    }

    private void getStep1Data(){

        party1Name = binding.step1Layout.partyAName.getText().toString().trim();
        party2Name = binding.step1Layout.partyBName.getText().toString().trim();
        party1Email = binding.step1Layout.partyAEmail.getText().toString().trim();
        party2Email = binding.step1Layout.partyBEmail.getText().toString().trim();

        if (party2Email.equals("") && party1Email.equals("")
                && party1Name.equals("") && party2Name.equals("")){
            Toast.makeText(ContractGenerate.this, "fill all credentials...", Toast.LENGTH_SHORT).show();
        }else {

            progressCounter = progressCounter + 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600); // 600ms nice smooth animation
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            binding.viewFlipper.showNext();


        }

    }
    private void getStep2Data(){

        contractTitle = binding.step2Layout.contractTitle.getText().toString().trim();
        contractType = binding.step2Layout.contractTypeSpinner.getSelectedItem().toString().trim();
        entityTypeA = binding.step2Layout.entityTypeA.getSelectedItem().toString().trim();
        entityTypeB = binding.step2Layout.entityTypeB.getSelectedItem().toString().trim();
        jurisdication = binding.step2Layout.endDate.getText().toString().trim();
        paymentTerms = binding.step2Layout.paymentTerms.getText().toString().trim();

        if (!(!contractType.equals("") || !contractTitle.equals("") ||
                !entityTypeA.equals("") || !entityTypeB.equals("") ||
                !startDate.equals("") || !endDate.equals("")
                || !jurisdication.equals("") )){
            Toast.makeText(this, "fill all credentials...", Toast.LENGTH_SHORT).show();
        }else {
            progressCounter = progressCounter + 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600); // 600ms nice smooth animation
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            binding.viewFlipper.showNext();

        }

    }
    private void getStep3Data(){

        generalTerms = binding.step3Layout.generalTerms.getText().toString().trim();
        specialClauses = binding.step3Layout.specialClauses.getText().toString().trim();

        if(generalTerms.equals("") && specialClauses.equals("")){
            Toast.makeText(this, "fill all credentials...", Toast.LENGTH_SHORT).show();
        }else{
            progressCounter = progressCounter + 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600); // 600ms nice smooth animation
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
            binding.viewFlipper.showNext();

        }

    }
    private void getStep4Data(){

        binding.step4Layout.partyANameTxt.setText(party1Name);
        binding.step4Layout.partyBNameTxt.setText(party2Name);
        binding.step4Layout.partyAEmailTxt.setText(party1Email);
        binding.step4Layout.partyBEmailTxt.setText(party2Email);

        binding.step4Layout.contractTitleTxt.setText(contractTitle);
        binding.step4Layout.contractTypeTxt.setText(contractType);
        binding.step4Layout.startDateTxt.setText(startDate);
        binding.step4Layout.endDateTxt.setText(endDate);
        binding.step4Layout.jurisdictionTxt.setText(jurisdication);
        binding.step4Layout.paymentTermsTxt.setText(paymentTerms);

        binding.step4Layout.generalTermsTxt.setText(generalTerms);
        binding.step4Layout.specialClausesTxt.setText(specialClauses);

    }


    private void generateContract() {
        String prompt = "Generate ONLY the contract text :\n\n" +
                "Your ROLE: Senior Legal Counsel specializing in " + jurisdication + " contract law\n" +
                "TASK: Draft a fully enforceable " + contractType + " compliant with " + jurisdication + " jurisdiction\n" +
                "FORMAT: Industry-standard legal document with section headers\n\n" +
                "with EXACTLY this structure:" +
                "## PARTIES\n" +
                "1. **" + party1Name + "** (" + entityTypeA + ")\n" +
                "   - Contact: " + party1Email + "\n" +
                "   - Legal Capacity: " + (entityTypeA.equals("Individual") ? "Natural Person" : "Corporate Entity") + "\n" +
                "2. **" + party2Name + "** (" + entityTypeB + ")\n" +
                "   - Contact: " + party2Email + "\n\n" +

                "## KEY TERMS\n" +
                "- Effective Date: " + (startDate.isEmpty() ? "[DATE]" : startDate) + "\n" +
                "- Termination: " + (endDate.isEmpty() ? "As per Clause X" : endDate) + "\n" +
                "- Governing Law: " + jurisdication + " (including all conflict of law provisions)\n\n" +

                "## DOCUMENT REQUIREMENTS\n" +
                "1. STRUCTURE:\n" +
                "   a. Title: '" + contractTitle + "' centered and bolded\n" +
                "   b. Recitals (Whereas clauses)\n" +
                "   c. Operative clauses with Arabic numerals (1, 2, 3)\n" +
                "   d. Signature blocks with date lines\n\n" +

                "2. MANDATORY CLAUSES:\n" +
                "   - Definitions (capitalized terms)\n" +
                "   - Representations & Warranties\n" +
                "   - " + (contractType.contains("NDA") ?
                "Confidentiality (including permitted disclosures and exclusions)" :
                "Performance Obligations") + "\n" +
                "   - Termination (including automatic renewal if applicable)\n" +
                "   - Force Majeure\n" +
                "   - Entire Agreement\n\n" +

                "3. SPECIAL PROVISIONS:\n" +
                "   - Include a severability clause\n" +
                "   - Jurisdiction-specific boilerplate for " + jurisdication + "\n" +
                "   - Electronic signature provision\n\n" +

                "## STYLE GUIDE\n" +
                "- Language: Plain English with precise legal terminology\n" +
                "- Tense: Shall for obligations, May for rights\n" +
                "- Defined terms: Capitalized and consistent\n" +
                "- Avoid: Legalese where possible without losing enforceability";


        // Call Gemini API
        RetrofitClient retrofitClient = new RetrofitClient("https://generativelanguage.googleapis.com/");
        GeminiApi geminiApi = retrofitClient.getRetrofit().create(GeminiApi.class);

        List<Part> parts = new ArrayList<>();
        parts.add(new Part(prompt));
        List<Content> contents = new ArrayList<>();
        contents.add(new Content(parts));
        RequestBodyGemini requestBody = new RequestBodyGemini(contents);

        Call<GeminiResponse> call = geminiApi.generateContent(API_KEY, requestBody);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseText = response.body()
                            .getCandidates()
                            .get(0)
                            .getContent()
                            .getParts()
                            .get(0)
                            .getText();
                    PreviewContractFragment generateContract = new PreviewContractFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("AI_RESPONSE", responseText);
                    generateContract.setArguments(bundle);
                    binding.container2.setVisibility(VISIBLE);
                    binding.stepProgress.setVisibility(GONE);
                    binding.viewFlipper.setVisibility(GONE);
// Then add the fragment to your activity
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.container_2,generateContract)
                            .commit();

                } else {
                    Toast.makeText(ContractGenerate.this, "Error generating contract", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Toast.makeText(ContractGenerate.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
