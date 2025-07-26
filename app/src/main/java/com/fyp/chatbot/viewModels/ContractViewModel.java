package com.fyp.chatbot.viewModels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.repository.GeminiRepo;

public class ContractViewModel extends ViewModel {
    MutableLiveData<String> contractResponse = new MutableLiveData<>();
    GeminiRepo repository = new GeminiRepo();

    public MutableLiveData<String> getContractResponse() {
        return contractResponse;
    }

    public void setContractResponse(String jurisdiction,String contractType,
                                    String contractTitle,String party1Name,
                                    String entityTypeA,String party2Name,
                                    String entityTypeB, String party1Email,
                                    String party2Email,String startDate,String endDate) {
        String prompt = generatContractPrompt(jurisdiction,contractType,contractTitle,party1Name,
                entityTypeA,party2Name,entityTypeB,party1Email,party2Email,startDate,endDate);
        repository.generateAnalysis(prompt, onContract -> {
            if (onContract != null) {
                contractResponse.postValue(onContract);
            }
        });

    }

    private String generatContractPrompt(String jurisdiction,String contractType,
                                         String contractTitle,String party1Name,
                                         String entityTypeA,String party2Name,
                                         String entityTypeB, String party1Email,
                                         String party2Email,String startDate,String endDate) {
        return "Generate ONLY the contract text :\n\n" +
                "Your ROLE: Senior Legal Counsel specializing in " + jurisdiction + " contract law\n" +
                "TASK: Draft a fully enforceable " + contractType + " compliant with " + jurisdiction + " jurisdiction\n" +
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
                "- Governing Law: " + jurisdiction + " (including all conflict of law provisions)\n\n" +

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
                "   - Jurisdiction-specific boilerplate for " + jurisdiction + "\n" +
                "   - Electronic signature provision\n\n" +

                "## STYLE GUIDE\n" +
                "- Language: Plain English with precise legal terminology\n" +
                "- Tense: Shall for obligations, May for rights\n" +
                "- Defined terms: Capitalized and consistent\n" +
                "- Avoid: Legalese where possible without losing enforceability";

    }
}
