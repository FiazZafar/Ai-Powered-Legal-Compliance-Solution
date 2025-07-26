package com.fyp.chatbot.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.fyp.chatbot.activities.ChatBot.API_KEY;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.fyp.chatbot.adapters.ComplianceAdapter;
import com.fyp.chatbot.apimodels.Content;
import com.fyp.chatbot.apimodels.GeminiResponse;
import com.fyp.chatbot.apimodels.Part;
import com.fyp.chatbot.apimodels.RequestBodyGemini;
import com.fyp.chatbot.databinding.FragmentComplianceCheckBinding;
import com.fyp.chatbot.helpers.RetrofitClient;
import com.fyp.chatbot.interfaces.GeminiApi;
import com.fyp.chatbot.models.ChecklistModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplianceCheck extends Fragment {

    private FragmentComplianceCheckBinding binding;

    private List<String> industryList;
    private List<String> countryList;
    private List<ChecklistModel> checklistModels;
    public ComplianceCheck() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentComplianceCheckBinding.inflate(inflater,container,false);
        View view = binding.getRoot();

        checklistModels = new ArrayList<>();

        countryList = Arrays.asList(
                "United States",
                "United Kingdom",
                "Canada",
                "Germany",
                "France",
                "Australia",
                "India",
                "China",
                "Japan",
                "South Korea",
                "Brazil",
                "South Africa",
                "Singapore",
                "Netherlands",
                "Switzerland",
                "United Arab Emirates",
                "Sweden",
                "Italy",
                "Mexico",
                "Pakistan");

        industryList = Arrays.asList(
                "Agriculture",
                "Automotive",
                "Banking & Finance",
                "Construction",
                "Consumer Goods",
                "Education",
                "Energy & Utilities",
                "Entertainment & Media",
                "Environmental Services",
                "Food & Beverage",
                "Government & Public Sector",
                "Healthcare & Pharmaceuticals",
                "Hospitality & Tourism",
                "Information Technology",
                "Insurance",
                "Legal Services",
                "Logistics & Transportation",
                "Manufacturing",
                "Mining & Natural Resources",
                "Non-Profit & NGOs",
                "Real Estate",
                "Retail & E-commerce",
                "Telecommunications",
                "Textile & Apparel",
                "Aerospace & Defense",
                "Cybersecurity",
                "Financial Technology (FinTech)",
                "Biotechnology",
                "Maritime & Shipping"
        );

         binding.spinnerIndustry.setAdapter(new ArrayAdapter(requireContext(),
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,industryList));
         binding.spinnerCountry.setAdapter(new ArrayAdapter<>(requireContext(),
                 androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,countryList));

        binding.complianceCheckLayout.complianceListRec.setLayoutManager(new LinearLayoutManager(requireContext(),
                LinearLayoutManager.VERTICAL,false));
        binding.complianceCheckLayout.complianceListRec.setAdapter(new ComplianceAdapter(checklistModels));

        binding.sendPromptBtn.setOnClickListener(view1 -> {
            String industry = binding.spinnerIndustry.getSelectedItem().toString();
            String country = binding.spinnerIndustry.getSelectedItem().toString();
            Boolean isDataPrivacy = binding.checkDataPrivacy.isSelected();
            Boolean isEnvironmental = binding.checkEnvironmental.isSelected();
            Boolean isFinancial = binding.checkFinancial.isSelected();
            sendRequestToAi(industry,country,isDataPrivacy,isEnvironmental,isFinancial);
        });




        return view;
    }

    private void sendRequestToAi(String industry,String country,Boolean isDataPrivacy,

                                 Boolean isEnvironmental,Boolean isFinancial) {

        binding.complianceCheckFliper.setVisibility(VISIBLE);
        binding.complianceCheckHeading.setVisibility(GONE);
        binding.spinnerCountry.setVisibility(GONE);
        binding.spinnerIndustry.setVisibility(GONE);
        binding.checkFinancial.setVisibility(GONE);
        binding.checkDataPrivacy.setVisibility(GONE);
        binding.checkEnvironmental.setVisibility(GONE);
        binding.sendPromptBtn.setVisibility(GONE);

        String prompt = "Act as a Chief Compliance Officer with 20+ years of multinational experience. " +
                    "Generate a exhaustive regulatory compliance checklist with:\n\n" +

                    "### COMPANY PROFILE\n" +
                    "- Industry: " + industry + " (Primary NAICS/SIC codes)\n" +
                    "- Jurisdiction: " + country + " (National + Local regulations)\n" +
                    "- Operations: Include cross-border considerations if applicable\n\n" +

                    "### COMPLIANCE PRIORITIES\n" +
                    (isDataPrivacy ? "■ DATA PRIVACY (GDPR/CCPA/PIPEDA etc.)\n" : "") +
                    (isEnvironmental ? "■ ENVIRONMENTAL (EPA/REACH/ROHS etc.)\n" : "") +
                    (isFinancial ? "■ FINANCIAL (SOX/AML/Basel III etc.)\n" : "") +
                    "■ GENERAL CORPORATE COMPLIANCE (Always included)\n\n" +

                    "### OUTPUT REQUIREMENTS\n" +
                    "1. For EACH requirement provide:\n" +
                    "   - Regulatory body & exact legal reference\n" +
                    "   - Implementation deadline (if time-bound)\n" +
                    "   - Penalty analysis (Fine ranges + Criminal liability)\n" +
                    "   - Risk mitigation steps\n" +
                    "   - Documentation requirements\n" +
                    "2. Categorize by:\n" +
                    "   - Priority (Critical/High/Medium/Low)\n" +
                    "   - Implementation complexity\n" +
                    "   - Cost impact\n\n" +

                    "### STRICT JSON FORMAT\n" +
                    "{\n" +
                    "  \"checklist\": [\n" +
                    "    {\n" +
                    "      \"id\": 1,\n" +
                    "      \"title\": \"[REGULATION] - [REQUIREMENT]\",\n" +
                    "      \"description\": \"[ACTIONABLE STEPS]\",\n" +
                    "      \"category\": \"data_privacy|environmental|financial|corporate\",\n" +
                    "      \"priority\": \"critical|high|medium|low\",\n" +
                    "      \"deadline\": \"YYYY-MM-DD|ongoing\",\n" +
                    "      \"legal_reference\": {\n" +
                    "        \"authority\": \"[REGULATORY BODY]\",\n" +
                    "        \"article\": \"[SPECIFIC CLAUSE]\"\n" +
                    "      },\n" +
                    "      \"penalties\": {\n" +
                    "        \"financial\": \"[FINE RANGE]\",\n" +
                    "        \"criminal\": \"[YES/NO]\",\n" +
                    "        \"reputational\": \"[RISK LEVEL]\"\n" +
                    "      },\n" +
                    "      \"implementation\": {\n" +
                    "        \"complexity\": \"high|medium|low\",\n" +
                    "        \"cost\": \"[USD RANGE]\",\n" +
                    "        \"documentation\": \"[REQUIRED DOCS]\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}\n\n" +

                    "### CRITICAL INSTRUCTIONS\n" +
                    "1. Cover ALL applicable:\n" +
                    "   - National laws\n" +
                    "   - Industry-specific regulations\n" +
                    "   - International treaties\n" +
                    "2. Flag requirements with:\n" +
                    "   - Personal liability for officers\n" +
                    "   - Whistleblower implications\n" +
                    "3. Include:\n" +
                    "   - Compliance calendar milestones\n" +
                    "   - Recommended audit frequency\n" +
                    "   - Common enforcement patterns";

        RetrofitClient retrofitClient = new RetrofitClient("https://generativelanguage.googleapis.com/");
        GeminiApi geminiApi = retrofitClient.getRetrofit().create(GeminiApi.class);

        RequestBodyGemini requestBodyGemini = new RequestBodyGemini(
                Collections.singletonList(new Content(Collections.singletonList(new Part(prompt)))));

        Call<GeminiResponse> geminiResponseCall = geminiApi.generateContent(API_KEY,requestBodyGemini);
        geminiResponseCall.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful()){
                    Log.d("RAW_RESPONSE", response.body().toString());

                    try {
                        Log.d("RAW_RESPONSE", response.body().toString());

                        String result = response
                                .body()
                                .getCandidates()
                                .get(0)
                                .getContent()
                                .getParts()
                                .get(0)
                                .getText();

                        processResponse(result);

                        Toast.makeText(requireContext(), "Response Parsed", Toast.LENGTH_SHORT).show();
                        binding.complianceCheckLayout.complianceCheckProgres.setVisibility(GONE);

                    } catch (Exception e){
                        Log.e("GeminiError", "JSON Parsing failed", e);
                        Toast.makeText(requireContext(), "Failed to parse response", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {

            }
        });
    }
    private void processResponse(String result) {
        try {
            Log.d("RESULT","DATA is " + result + "processResponse: ");
            // Extract JSON from response (handles cases where response has extra text)
            int jsonStart = result.indexOf("{");
            int jsonEnd = result.lastIndexOf("}") + 1;
            String jsonString = result.substring(jsonStart, jsonEnd);

            JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
            JsonArray checklistArray = object.getAsJsonArray("checklist");

            List<ChecklistModel> newList = new ArrayList<>();
            for (JsonElement item : checklistArray) {
                JsonObject obj = item.getAsJsonObject();
                newList.add(new ChecklistModel(
                        obj.get("id").getAsInt(),
                        obj.get("title").getAsString(),
                        obj.get("description").getAsString(),
                        obj.get("category").getAsString()
                ));
            }
            checklistModels.clear();
            checklistModels.addAll(newList);
            binding.complianceCheckLayout.complianceListRec.getAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            Log.e("PARSING_ERROR", "Failed at: " + result, e);
        }
    }
}