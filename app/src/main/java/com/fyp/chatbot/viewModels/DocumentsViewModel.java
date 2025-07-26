package com.fyp.chatbot.viewModels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fyp.chatbot.repository.GeminiRepo;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DocumentsViewModel extends ViewModel {
    private final GeminiRepo geminiRepo = new GeminiRepo();
    private final MutableLiveData<String> aiResult = new MutableLiveData<>();


    public MutableLiveData<String> getAiResult() {
        return aiResult;
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
                case "Jurisdiction Identifier":
                    prompt = createJurisdictionFinderPrompt(chunk);
                    break;
                case "Compliance Checker":

                    prompt = createComplianceCheckPrompt(chunk,"Pakistan");
                    break;
                default:
                    prompt = createObligationExtractorPrompt(chunk);
            }
            geminiRepo.generateAnalysis(prompt, result -> {
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
                                               int totalParts,
                                               String chunkText) {
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
    private String createComplianceCheckPrompt(String chunkText, String jurisdiction) {
        return "Analyze the following excerpt from a legal or business contract **silently and directly**. " +
                "Your task is to identify any **compliance-relevant clauses**, if any, based solely on the visible content below. " +
                "Do **not** include any introductions, summaries, or numbered labels.\n\n" +

                "### **Format strictly using bold labels (e.g., `**Compliance Area**:`)**\n" +
                "- Use the exact following format for each clause (repeat if needed):  \n\n" +
                "**Compliance Area**: [e.g., Data Privacy, Anti-Corruption, Employment Law, etc.]  \n" +
                "- **Clause Reference**:  Quote the exact sentence or provide a brief, clear summary of the clause.  \n" +
                "- **Status**: Compliant / Non-Compliant / Ambiguous  \n" +
                "- **Rationale**: Brief justification for why this status applies.  \n" +
                "- **Suggested Amendment**:  \n" +
                "  - Provide a correction if non-compliant.  \n\n" +

                "### **Compliance Detection Guidelines:**\n" +
                "- ✅ Data protection & privacy obligations (GDPR, HIPAA, etc.)  \n" +
                "- ✅ Intellectual property ownership and license grants  \n" +
                "- ✅ Anti-bribery, anti-corruption, and ethical compliance  \n" +
                "- ✅ Labor law, health & safety, and regulatory adherence  \n" +
                "- ✅ Tax and financial reporting obligations  \n" +
                "- ✅ Payment terms, liability limitations, and dispute resolution  \n" +
                "- ✅ Jurisdiction-specific statutory compliance (" + jurisdiction + " laws only)  \n\n" +

                "If no compliance-relevant clauses are found, respond only with: `-`\n\n" +
                "**Document Excerpt:**\n" + chunkText;
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
        return "You are analyzing **one part of a larger legal or business document**. " +
                "Identify all **confidentiality-related clauses** strictly from the text below.\n\n" +
                "### For each clause found, respond in this exact order:\n" +
                " **Clause Type**: Unilateral, Mutual, Non-standard, or `Unspecified` if unclear\n" +
                "- **Clause Text**: Exact wording mentioning confidentiality or non-disclosure\n" +
                "- **Scope**: What is considered confidential?\n\n" +
                "### Rules:\n" +
                "- Use only this text; do not infer from other parts of the document\n" +
                "- Include terms like 'confidential', 'non-disclosure', 'proprietary information'\n" +
                "- Ignore general boilerplate unless it has legal weight\n" +
                "- If no confidentiality clause is found in this section, respond with: `-`\n\n" +
                "**Document Section to Analyze:**\n" + chunkText;
    }

    private String createJurisdictionFinderPrompt(String chunkText) {
        return "You are analyzing **one part of a larger legal or business document**. " +
                "Identify any **jurisdiction-related clauses** strictly from the provided text below.\n\n" +
                "### For each clause found, use this exact format:\n" +
                " **Clause Text**: Exact wording of the governing law/jurisdiction clause\n" +
                "- **Jurisdiction**: Country, state, or region (only if explicitly mentioned)\n" +
                "- **Court Venue**: Specific court or venue (only if explicitly mentioned)\n" +
                "- **Enforceability Notes**: Only if there is ambiguity or unusual enforcement terms; otherwise omit\n\n" +
                "### Rules:\n" +
                "- Analyze only this section; do not infer from missing parts of the document\n" +
                "- Do not explain what jurisdiction means\n" +
                "- If no jurisdiction clause is found in this section, respond with: `-`\n\n" +
                "**Document Section to Analyze:**\n" + chunkText;
    }
    private String createObligationExtractorPrompt(String chunkText) {
        return "Analyze the following excerpt from a legal contract **silently and directly**. " +
                "Your task is to identify all **legal obligations**, if any, based solely on the visible content below. " +
                "Do **not** include any introductions, summaries, bullet numbers, or extra commentary.\n\n" +

                "### **Format strictly using bold labels for each obligation (repeat if multiple):**\n" +
                "**Party Responsible**: [Buyer / Seller / Client / Vendor, etc.]  \n" +
                "- **Obligation Description**: [What specific action must be performed?]  \n" +
                "- **Due Date / Frequency**: [If specified]  \n" +
                "- **Linked Clause**:  \n" +
                "   Quote the exact clause text or a clear snippet.  \n\n" +

                "### **Obligation Extraction Guidelines:**\n" +
                "- ✅ Focus on mandatory terms: “shall,” “must,” “is required to,” “agrees to,” etc.  \n" +
                "- ✅ Include recurring or time-bound obligations (e.g., monthly reporting, delivery deadlines).  \n" +
                "- ✅ Capture obligations for all parties, not just one side.  \n" +
                "- ⚠ If no obligations are present in this section, respond only with: `-`\n\n" +

                "**Document Excerpt:**\n" + chunkText;
    }

}
