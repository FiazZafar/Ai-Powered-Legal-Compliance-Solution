package com.fyp.chatbot.viewModels;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.interfaces.FirebaseCallback;
import com.fyp.chatbot.repository.GeminiRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class SummarizationViewModel extends ViewModel {
    private final GeminiRepository geminiRepository = new GeminiRepository();
    private final MutableLiveData<String> aiResult = new MutableLiveData<>();


    public MutableLiveData<String> getAiResult() {
        return aiResult;
    }


    public void saveDocment() {
    }


    public void setAiResponse(String extractedText, String taskType) {
        int chunkSize = 1000;
        int totalChunks = (int) Math.ceil((double) extractedText.length() / chunkSize);
        Map<Integer, String> orderedResponses = new ConcurrentHashMap<>();
        AtomicInteger completedChunks = new AtomicInteger(0);
        AtomicBoolean quotaExceeded = new AtomicBoolean(false);

        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, extractedText.length());
            String chunk = extractedText.substring(start, end);
            int chunkIndex = i;

            if (quotaExceeded.get()) {
                break;
            }

            String prompt;

            switch (taskType) {
                case "Contract Summarizer":
                    prompt = createContractSummaryPrompt(chunkIndex + 1, totalChunks, chunk);
                    break;
                case "Risk Clause Detector":
                    prompt = createRiskClausePrompt(chunk);
                    break;
                case "Confidentiality Clause Tracker":
                    prompt = createConfidentialityClausePrompt(chunk);
                    break;
                default:
                    prompt = createLegalAnalysisPrompt(chunk, "Pakistan");
            }
            geminiRepository.generateAnalysis(prompt, result -> {
                // If quota error detected
                if (result.contains("RESOURCE_EXHAUSTED")) {
                    quotaExceeded.set(true);
                    orderedResponses.put(chunkIndex, "⚠️ The AI service is currently unavailable " +
                            "due to request limits. Please try again after some time.");
                } else {
                    orderedResponses.put(chunkIndex, result.trim());
                }

                if (completedChunks.incrementAndGet() == totalChunks || quotaExceeded.get()) {
                    StringBuilder finalSummary = new StringBuilder();
                    Set<Integer> seen = new HashSet<>();
                    for (int j = 0; j < totalChunks; j++) {
                        String part = orderedResponses.getOrDefault(j, "");
                        if (!seen.contains(j) && !part.trim().equals("-") && !part.trim().isEmpty()) {
                            finalSummary.append(part).append("\n\n");
                            seen.add(j);
                            if (quotaExceeded.get()) break; // Only include partial output if quota hit
                        }
                    }

                    if (quotaExceeded.get()) {
                        finalSummary.append("🔒 Parts were skipped due to AI usage limits." +
                                " Please try again later.");
                    }

                    aiResult.postValue(finalSummary.toString().trim());
                }
            });
        }
    }


    private String createContractSummaryPrompt(int partNumber,
                                                    int totalParts, String chunkText) {
        Map<String, String> sectionMap = new LinkedHashMap<>();
        sectionMap.put("Document Type", "**Document Type**\n- Classify as Contract / Policy / Filing etc.");
        sectionMap.put("Key Entities", "**Key Entities**\n- Parties, Jurisdiction");
        sectionMap.put("Obligations", "✅ **Obligations / Rules**\n- Bullet-pointed duties");
        sectionMap.put("Critical Dates", "⚠️ **Critical Dates**\n- Deadlines, renewal terms");
        sectionMap.put("Notable Clauses", "🔍 **Notable Clauses**\n- Top 2–3 impactful sections");
        sectionMap.put("Confidentiality", "🔒 **Confidentiality**\n- Scope, obligations");
        sectionMap.put("Enforcement", "⚖️ **Penalties / Enforcement**\n- Non-compliance outcomes");
        sectionMap.put("Jurisdiction", "🌐 **Jurisdiction**\n- Governing law, region");

        StringBuilder includedSections = new StringBuilder();
        for (Map.Entry<String, String> entry : sectionMap.entrySet()) {
            if (chunkText.toLowerCase().contains(entry.getKey().toLowerCase())) {
                includedSections.append(entry.getValue()).append("\n\n");
            }
        }

        if (includedSections.length() == 0) {
            includedSections.append("📄 This section has general legal text. No specific clauses detected.\n");
        }

        return "**Legal Summary – Part " + partNumber + " of " + totalParts + "**\n\n" +
                "Please analyze **only this part** of the document. Return a concise, formal summary using the structure below (if applicable).\n" +
                "- Focus only on sections found in the text.\n" +
                "- Do NOT repeat earlier parts or explain overall context.\n" +
                "- Use markdown formatting.\n\n" +
                includedSections.toString() +
                "\n**Document Text**:\n" + chunkText;
    }



    private String createComplianceCheckPrompt(String extractedText, String jurisdiction, List<String> complianceAreas) {
        return "**Contract Compliance Audit Report** (" + jurisdiction + ")\n\n" +

                "Review the contract below and check its alignment with the following compliance areas:\n" +
                complianceAreas.stream()
                        .map(area -> "- " + area)
                        .collect(Collectors.joining("\n")) + "\n\n" +

                "### Output Format:\n" +
                "- **Compliance Area**: [e.g., Data Protection / Labor Laws / Tax]\n" +
                "- **Clause Reference**: [Clause number and summary text]\n" +
                "- **Compliance Status**: [Compliant / Non-Compliant / Ambiguous]\n" +
                "- **Rationale**: [Brief explanation for status]\n" +
                "- **Suggested Amendment**: [Optional correction for non-compliance]\n\n" +

                "**Guidelines:**\n" +
                "- Refer to " + jurisdiction + " laws or industry-specific standards\n" +
                "- Highlight gaps, ambiguities, or violations\n" +
                "- Focus on clauses relevant to the listed compliance areas only\n\n" +

                "**Contract for Review:**\n" ;
    }
    private String createRiskClausePrompt(String chunkText) {
        return "Analyze the following excerpt from a legal or business document **silently and directly**. " +
                "Your task is to identify potential **risks**, if any, based solely on the visible content below. " +
                "Do **not** include any introductions, summaries, or numbered risk labels.\n\n" +

                "### **Format strictly using bold labels (e.g., `**Risk Type**:`)**\n" +
                "- Use the exact following format for each risk (repeat if needed):  \n\n" +
                "**Risk Type**: [Financial / Legal / Operational / Compliance]  \n" +
                "- **Excerpt**:  \n" +
                " > Quote the exact sentence or clause that poses the risk.  \n" +
                "- **Severity**: 🟥 High / 🟧 Medium / 🟩 Low — briefly justify  \n" +
                "- **Suggested Mitigation**:  \n" +
                "  - Short advice to reduce or address the risk  \n\n" +

                "### **Risk Detection Guidelines:**\n" +
                "- ✅ Vague or broad language: “reasonable efforts,” “as appropriate”  \n" +
                "- ✅ One-sided obligations: unlimited indemnity, strong penalties  \n" +
                "- ✅ Compliance gaps: GDPR, HIPAA, export laws, etc.  \n" +
                "- ✅ Uncapped financial liabilities or hidden obligations  \n\n" +

                "If no risks are identified, respond only with: `-`\n\n" +
                "**Document Excerpt:**\n" + chunkText;
    }
    private String createConfidentialityClausePrompt(String chunkText) {
        return "You are reviewing a section of a legal or business document. " +
                "Identify any **confidentiality-related clauses** within the visible text below.\n\n" +
                "### For each clause found, provide:\n" +
                "- **Clause Number**: [If available, e.g., §5.3]\n" +
                "- **Clause Text**: Exact wording mentioning confidentiality or non-disclosure\n" +
                "- **Type**: `Unilateral`, `Mutual`, or `Non-standard`\n" +
                "- **Scope**: What is considered confidential?\n" +
                "- **Term Duration**: How long confidentiality obligations apply\n\n" +
                "### Detection Guidelines:\n" +
                "- Include terms like: *confidential*, *non-disclosure*, *proprietary information*\n" +
                "- Ignore general boilerplate unless it has legal weight\n" +
                "- ❗ If no clause is found in this section, respond with: `-` (just a dash — nothing else)\n\n" +
                "**Visible Document Section to Analyze:**\n" + chunkText;
    }

    private String createJurisdictionFinderPrompt(String extractedText) {
        return "**Jurisdiction Identification Report**\n\n" +
                "Scan the contract and extract all jurisdiction-related details.\n\n" +
                "### Output Format:\n" +
                "- **Clause Text**: [Complete governing law/jurisdiction clause]\n" +
                "- **Jurisdiction**: [Country/State/Region]\n" +
                "- **Court Venue**: [If specified]\n" +
                "- **Enforceability Notes**: [Optional AI insights if ambiguous]\n\n" +
                "**Instruction Notes**:\n" +
                "- Search for 'governing law', 'venue', 'dispute resolution', etc.\n" +
                "- Return null if jurisdiction is not defined\n\n" +
                "**Document to Process**:\n" ;
    }
    private String createObligationExtractorPrompt(String extractedText) {
        return "**Legal Obligations Extractor**\n\n" +
                "Extract clear obligations from the following contract.\n\n" +
                "### Output Format:\n" +
                "- **Party Responsible**: [E.g., Buyer / Seller / Client / Vendor]\n" +
                "- **Obligation Description**: [What must be done?]\n" +
                "- **Due Date / Frequency**: [If specified]\n" +
                "- **Linked Clause**: [Clause number or snippet]\n\n" +
                "**Instructions**:\n" +
                "- Focus on words like 'shall', 'must', 'agrees to', etc.\n" +
                "- Return at least 5 key obligations, if available\n\n" +
                "**Contract**:\n" ;
    }
    private String createLegalAnalysisPrompt(String extractedText, String jurisdiction) {
        return "**Professional Contract Analysis Report** (" + jurisdiction + ")\n\n" +

                "### 1. Key Clause Summary\n" +
                "#### A. Core Terms\n" +
                "- **Payment Terms**: [Neutral description of clauses]\n" +
                "- **Termination**: [Conditions and notice periods]\n" +
                "- **Confidentiality**: [Scope and duration]\n\n" +

                "#### B. Special Provisions\n" +
                "- **Governing Law**: " + jurisdiction + " [Specific Law Reference]\n" +
                "- **Dispute Resolution**: [Mechanism specified]\n\n" +

                "### 2. Risk Assessment\n" +
                "#### Potential Concerns\n" +
                "- **Ambiguity**: \"[Exact vague phrase]\" could be clarified\n" +
                "- **Omission**: Missing [standard clause type]\n" +
                "- **Unbalanced Terms**: [Party]-favorable clause in §X.Y\n\n" +

                "### 3. Improvement Suggestions\n" +
                "#### Recommended Additions\n" +
                "```legal\n" +
                "\"All notices shall be in writing and delivered via registered mail to [Address].\"\n" +
                "```\n\n" +

                "#### Language Enhancements\n" +
                "- Replace \"[problematic phrase]\" with \"[clearer alternative]\"\n" +
                "- Define \"[ambiguous term]\" in Definitions section\n\n" +

                "### 4. " + jurisdiction + "-Specific Notes\n" +
                "- Required: [Mandatory local clause]\n" +
                "- Recommended: [Common local practice]\n\n" +

                "**Analysis Protocol**:\n" +
                "- Focus on clarity and completeness\n" +
                "- Avoid compliance judgments\n" +
                "- Contract Text Provided:\n" ;
    }

}
