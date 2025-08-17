package com.fyp.chatbot.activities;


import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.fyp.chatbot.R;
import com.fyp.chatbot.databinding.ActivityContractGenerateBinding;
import com.fyp.chatbot.fragments.PreviewContractFragment;
import com.fyp.chatbot.viewModels.ContractViewModel;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ContractGenerate extends AppCompatActivity {

    private ActivityContractGenerateBinding binding;

    private String party1Name,party2Name,party1Email,party2Email;
    private String contractTitle,contractType,entityTypeA,entityTypeB,
            startDate,endDate,paymentTerms,jurisdiction;
    private String generalTerms,specialClauses;
    private Calendar calendar;
    private int progressCounter;
    private ObjectAnimator animation;
    ContractViewModel contractMVVM ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityContractGenerateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contractMVVM = new ViewModelProvider(this).get(ContractViewModel.class);

        binding.step4Layout.generateButton.setEnabled(true);
        binding.step4Layout.generateButton.setAlpha(1.0f);
        binding.step4Layout.editButton.setEnabled(true);
        binding.step4Layout.editButton.setAlpha(1.0f);

        generateContract();
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
                    (datePicker1, i, i1, i2) -> {
                        endDate = endDay + "/" + endMonth + "/" + endYear;
                        binding.step2Layout.endDate.setText(endDate);
                    },endYear,endMonth,endDay);
            datePicker.show();
        });
        binding.step1Layout.nextBtnStep1.setOnClickListener(view -> getStep1Data());
        binding.step2Layout.nextBtnStep2.setOnClickListener(view -> getStep2Data());
        binding.step3Layout.nextBtnStep3.setOnClickListener(view -> {
            getStep3Data();
            getStep4Data();
        });

        binding.step4Layout.generateButton.setOnClickListener(view -> {

            binding.step4Layout.generateButton.setAlpha(0.5f);
            binding.step4Layout.generateButton.setEnabled(false);
            binding.step4Layout.editButton.setAlpha(0.5f);
            binding.step4Layout.editButton.setEnabled(false);

            binding.linearlayout2.setVisibility(VISIBLE);

            contractMVVM.setContractResponse(jurisdiction,contractType
                    ,contractTitle,party1Name,
                    entityTypeA,party2Name
                    ,entityTypeB,party1Email
                    ,party2Email,startDate,endDate);

                });

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


        List<String> contractTypes = Arrays.asList(
                "Freelance Agreement",
                "NDA (Non-Disclosure Agreement)",
                "Employment Contract",
                "Rental Agreement",
                "Partnership Agreement",
                "Service Agreement",
                "Loan Agreement"
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

        if (party2Email.isEmpty() && party1Email.isEmpty()
                && party1Name.isEmpty() && party2Name.isEmpty()){
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
        jurisdiction = binding.step2Layout.jurisdiction.getText().toString().trim();
        paymentTerms = binding.step2Layout.paymentTerms.getText().toString().trim();

        if (!(!contractType.isEmpty() || !contractTitle.isEmpty() ||
                !entityTypeA.isEmpty() || !entityTypeB.isEmpty() ||
                !startDate.isEmpty() || !endDate.isEmpty()
                || !jurisdiction.isEmpty())){
            Toast.makeText(this, "fill all credentials...", Toast.LENGTH_SHORT).show();
        } else {
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

        if(generalTerms.isEmpty() && specialClauses.isEmpty()){
            Toast.makeText(this, "fill all credentials...", Toast.LENGTH_SHORT).show();
        }else{
            progressCounter = progressCounter + 33;
            animation = ObjectAnimator.ofInt(binding.stepProgress, "progress", binding.stepProgress.getProgress(), progressCounter);
            animation.setDuration(600);
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
        binding.step4Layout.jurisdictionTxt.setText(jurisdiction);
        if (paymentTerms.isEmpty() || paymentTerms == null){
            binding.step4Layout.paymentTermsTxt.setVisibility(GONE);
            binding.step4Layout.paymentTxtHeading.setVisibility(GONE);
        }else {
            binding.step4Layout.paymentTxtHeading.setVisibility(VISIBLE);
            binding.step4Layout.paymentTermsTxt.setVisibility(VISIBLE);
            binding.step4Layout.paymentTermsTxt.setText(paymentTerms);
        }
        binding.step4Layout.generalTermsTxt.setText(generalTerms);
        binding.step4Layout.specialClausesTxt.setText(specialClauses);

    }


    private void generateContract() {
        contractMVVM.getContractResponse().observe(this,onResponseText -> {
            binding.linearlayout2.setVisibility(GONE);
            PreviewContractFragment generateContract = new PreviewContractFragment();
            Bundle bundle = new Bundle();
            bundle.putString("AI_RESPONSE", onResponseText);
            generateContract.setArguments(bundle);
            binding.container2.setVisibility(VISIBLE);
            binding.stepProgress.setVisibility(GONE);
            binding.viewFlipper.setVisibility(GONE);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_2, generateContract)
                    .commit();
        });
    }

}
