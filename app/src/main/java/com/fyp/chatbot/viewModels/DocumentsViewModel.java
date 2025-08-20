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
                    prompt = createContractSummaryPrompt(chunkIndex + 1, chunk);
                    break;
                case "Risk Clause Detector":
                    prompt = createRiskClausePrompt(chunkIndex + 1,chunk);
                    break;
                case "Confidentiality Clause Tracker":
                    prompt = createConfidentialityClausePrompt(chunkIndex + 1,chunk);
                    break;
                case "Jurisdiction Identifier":
                    prompt = createJurisdictionFinderPrompt(chunkIndex + 1,chunk);
                    break;
                case "Compliance Checker":

                    prompt = createComplianceCheckPrompt(chunkIndex + 1,chunk,"Pakistan");
                    break;
                default:
                    prompt = createObligationExtractorPrompt(chunkIndex + 1 ,chunk);
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
    private String createContractSummaryPrompt(int partNumber, String chunkText) {
        boolean isFirst = (partNumber == 1);

        StringBuilder sections = new StringBuilder();
        if (isFirst) {
            sections.append("**Document Type**\n- Explain in 2–3 lines what type of document this is (like contract, policy, filing). Keep it simple.\n\n");
            sections.append("**Key Entities**\n- Mention parties, companies, or people. Also note the region or jurisdiction in 2–3 lines.\n\n");
        }

        sections.append("**Obligations / Rules**\n- Write the duties or rules given here. Explain in 2–3 lines per point so user understands clearly.\n\n");
        sections.append("**Critical Dates**\n- Note any dates like deadlines, renewals, or expiry. Explain why each is important in 2–3 lines.\n\n");
        sections.append("**Notable Clauses**\n- Pick top 2–3 important clauses. Write each in simple English, 2–3 lines per bullet.\n\n");
        sections.append("**Confidentiality**\n- If present, explain in 2–3 lines what is confidential and what duties apply.\n\n");
        sections.append("**Penalties / Enforcement**\n- If given, write in 2–3 lines what happens if rules are broken.\n\n");
        if (isFirst) {
            sections.append("**Jurisdiction**\n- Write in 2–3 lines the law or region that controls this document.\n\n");
        }

        return ""
                + "You are analyzing a part of a long legal or business document. "
                + "Do not mention that this is only a part. Just focus on this text only.\n"
                + "Hidden flag for you (do not show to user): FIRST_EXCERPT=" + (isFirst ? "YES" : "NO") + "\n\n"

                + "Instructions:\n"
                + "- Use the sections listed below only.\n"
                + "- If FIRST_EXCERPT=NO, skip general info like Document Type, Key Entities, Jurisdiction.\n"
                + "- Each bullet must be at least 3–4 lines, easy English.\n"
                + "- If information is missing or incomplete, **skip that section** (don’t write half points).\n"
                + "- No intro, no conclusion, no numbering, no mention of chunks or parts.\n"
                + "- If nothing useful is in the text, respond only with `-`.\n\n"

                + "Sections you can use:\n"
                + sections.toString()
                + "\n**Document Excerpt:**\n" + chunkText;
    }

    private String createComplianceCheckPrompt(int partNumber, String chunkText, String jurisdiction) {
        String base = ""
                + "This is Part " + partNumber + " of the document. "
                + "Do not mention anywhere that this is a part or chunk. "
                + "Just check only the given text.\n\n"

                + "----------------------------\n"
                + "      Compliance Check      \n"
                + "----------------------------\n\n"


                + "Instructions:\n"
                + "- Find possible compliance check only from this text.\n"
                + "- Use the format given below for each compliance check.\n"
                + "- Each answer should be 2–3 easy lines, not just 1 short line.\n"
                + "- Write in clear and simple English (like Pakistani style easy English).\n"
                + "- If the text looks incomplete or unclear, just skip it instead of half answers.\n"
                + "- Do not add intros, conclusions, or mention of parts/chunks.\n"
                + "- If no compliance check are found, respond only with: `-`.\n\n"


                + "### **Format (follow this strictly for every clause):**\n"
                + "**Compliance Area**: [e.g., Data Privacy, Anti-Corruption, Employment Law]  \n"
                + "- **Clause Reference**: \n"
                + "  > Copy the exact line or give a short, clear summary.  \n"
                + "- **Status (Check Result)**: Compliant / Non-Compliant / Ambiguous  \n"
                + "  - Write in 2–3 simple lines why this status applies.  \n"
                + "- **Suggested Amendment (Fix if Needed)**:  \n"
                + "  - Give short and clear advice (2–3 lines) to correct or improve it.  \n\n"

                + "### **Compliance Detection Hints (to guide you, don’t show user):**\n"
                + "- ✅ Data protection & privacy (GDPR, HIPAA, etc.)\n"
                + "- ✅ Intellectual property rights and license rules\n"
                + "- ✅ Anti-bribery, anti-corruption, and ethical standards\n"
                + "- ✅ Labor law, health & safety, and employee rights\n"
                + "- ✅ Tax and financial reporting rules\n"
                + "- ✅ Payment terms, dispute settlement, and liability limits\n";

        // Sirf Part 1 mein jurisdiction ka hint
        if (partNumber == 1) {
            base += "- ✅ Jurisdiction-specific rules (" + jurisdiction + " laws only)\n";
        }

        base += "\nIf no compliance issues are found, respond only with: `-`\n\n"
                + "**Document Excerpt (Part " + partNumber + "):**\n" + chunkText;

        return base;
    }

    private String createRiskClausePrompt(int partNumber, String chunkText) {
        return ""
                + "This is Part " + partNumber + " of the document. "
                + "Do not mention anywhere that this is a part or chunk. "
                + "Just analyze only the given text.\n\n"

                + "----------------------------\n"
                + "        Risk Analysis        \n"
                + "----------------------------\n\n"

                + "Instructions:\n"
                + "- Find possible risks only from this text.\n"
                + "- Use the format given below for each risk.\n"
                + "- Each answer should be 2–3 easy lines, not just 1 short line.\n"
                + "- Write in clear and simple English (like Pakistani style easy English).\n"
                + "- If the text looks incomplete or unclear, just skip it instead of half answers.\n"
                + "- Do not add intros, conclusions, or mention of parts/chunks.\n"
                + "- If no risks are found, respond only with: `-`.\n\n"

                + "### **Format (repeat for every risk):**\n"
                + "**Risk Type**: Financial / Legal / Work / Compliance  \n"
                + "- **Excerpt**:  \n"
                + " > Copy the exact line or sentence that shows the risk.  \n"
                + "- **Severity (How Big the Risk Is)**: 🟥 High / 🟧 Medium / 🟩 Low  \n"
                + "  - Explain in 2–3 simple lines why this level is chosen.  \n"
                + "- **Mitigation (How to Fix or Reduce the Risk)**:  \n"
                + "  - Give short and clear advice (2–3 lines) to avoid or reduce the problem.  \n\n"

                + "### **Risk Detection Hints (for you only, don’t show user):**\n"
                + "- Very general words like “reasonable efforts” or “as appropriate.”\n"
                + "- One-sided promises like unlimited penalty or strong fine.\n"
                + "- Missing privacy or law rules (GDPR, HIPAA, export laws, etc.).\n"
                + "- Money loss or hidden costs without clear limits.\n\n"

                + "**Document Excerpt (Part " + partNumber + "):**\n" + chunkText;
    }
    private String createConfidentialityClausePrompt(int partNumber, String chunkText) {
        String base = ""
                + "This is Part " + partNumber + " of the document. "
                + "Do not mention anywhere that this is a part or chunk. "
                + "Just check only the given text.\n\n"

                + "----------------------------\n"
                + " Confidentiality Check \n"
                + "----------------------------\n\n"

                + "Instructions:\n"
                + "- Find possible confidentiality clause only from this text.\n"
                + "- Use the format given below for each confidentiality clause.\n"
                + "- Each answer should be 3–4 easy lines, not just 1 short line.\n"
                + "- Write in clear and simple English (like Pakistani style easy English).\n"
                + "- If the text looks incomplete or unclear, just skip it instead of half answers.\n"
                + "- Do not add intros, conclusions, or mention of parts/chunks.\n"
                + "- If no confidentiality clause are found, respond only with: `-`.\n\n"

                + "### **Format (follow strictly for each clause):**\n"
                + "**Clause Type**: Unilateral / Mutual / Non-standard / Unspecified  \n"
                + "- **Clause Text**:  \n"
                + "  > Copy the exact wording that mentions confidentiality, non-disclosure, or proprietary info.  \n"
                + "- **Scope (What’s Covered)**:  \n"
                + "  - Explain in 2–3 clear lines what is treated as confidential (e.g., data, business info, trade secrets).  \n"
                + "- **Notes (if any special condition)**:  \n"
                + "  - Write short and clear explanation if the clause has unique or unusual terms.  \n\n"

                + "### **Detection Hints (to guide you, don’t show user):**\n"
                + "- ✅ Look for terms like 'confidential', 'non-disclosure', 'proprietary information'.\n"
                + "- ✅ Identify if the obligation is **one-sided (unilateral)** or **mutual**.\n"
                + "- ✅ Ignore general boilerplate unless it clearly creates legal duty.\n"
                + "- ✅ Highlight if the clause is vague, very broad, or unclear.\n\n"

                + "If no confidentiality clause is found, respond only with: `-`\n\n"
                + "**Document Excerpt (Part " + partNumber + "):**\n" + chunkText;

        return base;
    }
    private String createJurisdictionFinderPrompt(int partNumber, String chunkText) {
        String base = ""
                + "This is Part " + partNumber + " of the document. "
                + "Do not mention that it is part or chunk. "
                + "Just analyze only the given text.\n\n"

                + "-----------------------------\n"
                + " Jurisdiction Check \n"
                + "-----------------------------\n\n"

                + "Instructions:\n"
                + "- Find possible jurisdiction check only from this text.\n"
                + "- Use the format given below for each jurisdiction check.\n"
                + "- Each answer should be 2–3 easy lines, not just 1 short line.\n"
                + "- Write in clear and simple English (like Pakistani style easy English).\n"
                + "- If the text looks incomplete or unclear, just skip it instead of half answers.\n"
                + "- Do not add intros, conclusions, or mention of parts/chunks.\n"
                + "- If no jurisdiction check are found, respond only with: `-`.\n\n"

                + "### **Format (use for each clause found):**\n"
                + "**Clause Text**:  \n"
                + "  - Copy the exact wording of the governing law or jurisdiction clause.  \n"
                + "- **Jurisdiction **: [If Given] \n"
                + "  - Mention the country, state, or region clearly if written.  \n"
                + "- **Court Venue **: [If Given] \n"
                + "  - State the specific court or location, but only if the text directly says it.  \n"
                + "- **Enforceability Notes**:  \n"
                + "  - Write only if there is something unusual, unclear, or special about enforcement. Otherwise skip.  \n\n"

                + "### **Detection Hints (for guidance only):**\n"
                + "- ✅ Look for words like: “governed by”, “under the laws of”, “exclusive jurisdiction”.  \n"
                + "- ✅ Only rely on this part of the document, ignore missing sections.  \n"
                + "- ❌ Do not explain the meaning of jurisdiction to the user.  \n\n"

                + "If no jurisdiction clause is present, respond only with: `-`\n\n"
                + "**Document Excerpt (Part " + partNumber + "):**\n" + chunkText;

        return base;
    }

    private String createObligationExtractorPrompt(int partNumber, String chunkText) {
        String base = ""
                + "This is Part " + partNumber + " of the document. "
                + "Do not mention that it is part or chunk. "
                + "Just analyze only the given text.\n\n"

                + "-------------------------\n"
                + " Obligation Check \n"
                + "-------------------------\n\n"

                + "Instructions:\n"
                + "- Find possible obligation check only from this text.\n"
                + "- Use the format given below for each obligation check.\n"
                + "- Each answer should be 2–3 easy lines, not just 1 short line.\n"
                + "- Write in clear and simple English (like Pakistani style easy English).\n"
                + "- If the text looks incomplete or unclear, just skip it instead of half answers.\n"
                + "- Do not add intros, conclusions, or mention of parts/chunks.\n"
                + "- If no obligation check are found, respond only with: `-`.\n\n"

                + "### **Format (use for each obligation found):**\n"
                + "**Party Responsible**: Buyer / Seller / Client / Vendor / Other  \n"
                + "- **Obligation (What Must Be Done)**:  \n"
                + "  - Write in simple words what action or duty is required. At least 3-4 lines.  \n"
                + "- **Timeline (Due Date / Frequency)**:  \n"
                + "  - Mention clearly if the text says when or how often this duty must be done.  \n"
                + "- **Clause Reference**:  \n"
                + "  > Copy the exact wording or short snippet that shows the obligation.  \n\n"

                + "### **Detection Hints (for guidance only):**\n"
                + "- ✅ Look for words like: “shall”, “must”, “is required to”, “agrees to”.  \n"
                + "- ✅ Include one-time and repeating duties (e.g., monthly reports, yearly audits).  \n"
                + "- ✅ Cover obligations for both sides (not just one party).  \n\n"

                + "If no obligation is found, respond only with: `-`\n\n"
                + "**Document Excerpt (Part " + partNumber + "):**\n" + chunkText;

        return base;
    }


}
