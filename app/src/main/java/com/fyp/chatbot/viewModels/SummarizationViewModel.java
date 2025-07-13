package com.fyp.chatbot.viewModels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.fyp.chatbot.repository.GeminiRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SummarizationViewModel extends ViewModel {
    private final GeminiRepository geminiRepository = new GeminiRepository();

    public LiveData<String> setAiResponse(String extractedText, String taskType) {
        if (extractedText.length() > 10000) {
            extractedText = extractedText.substring(0, 10000);
            int lastPeriod = extractedText.lastIndexOf(".");
            if (lastPeriod > 0) {
                extractedText = extractedText.substring(0, lastPeriod + 1);
            }
        }
        List<String> complianceAreas = Arrays.asList("Data Protection & Privacy (e.g., GDPR/CCPA)"
                ,"Employment & Labor Compliance","Financial Disclosure Requirements",
                "Anti-Bribery & Anti-Corruption","Healthcare or HIPAA Regulations",
                "Tax & Regulatory Reporting","Environmental & Safety Standards");
        String prompt;
        switch (taskType) {
            case "Contract Summarizer":
                prompt = createContractSummaryPrompt(extractedText);
                break;
            case "Risk Clause Detector":
                prompt = createRiskClausePrompt(extractedText);
                break;
            case "Confidentiality Clause Tracker":
                prompt = createConfidentialityClausePrompt(extractedText);
                break;
            case "Jurisdiction Identifier":
                prompt = createJurisdictionFinderPrompt(extractedText);
                break;
            case "Compliance Checker":
                prompt = createComplianceCheckPrompt(extractedText, "Japan",complianceAreas);
                break;
            case "Obligation Extractor":
                prompt = createObligationExtractorPrompt(extractedText);
                break;
            default:
                prompt = createLegalAnalysisPrompt(extractedText, "Pakistan");
                break;
        }

        return geminiRepository.generateAnalysis(prompt);
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

                "**Contract for Review:**\n" + extractedText;
    }

    private String createContractSummaryPrompt(String extractedText) {
        return "**Generate a Structured Legal/Business Summary**  \n" +
                "Analyze the uploaded document and provide a concise overview tailored to its content type.  \n" +
                "\n" +
                "### **Sections Required**  \n" +
                "1. **Document Type**  \n" +
                "   - Classify: `Contract` / `Policy` / `Regulatory Filing` / `Corporate Agreement` / `Other`.  \n" +
                "   - Subtype (if applicable): e.g., \"NDA,\" \"Employment Policy,\" \"SEC Filing.\"  \n" +
                "\n" +
                "2. **Key Entities**  \n" +
                "   - **Parties/Stakeholders**: Names, roles (e.g., \"Vendor: ABC Inc., Client: XYZ Corp\").  \n" +
                "   - **Jurisdiction**: Governing law/region (if mentioned).  \n" +
                "\n" +
                "3. **Core Provisions**  \n" +
                "   - ✅ **Obligations/Rules**: Bullet-pointed duties/requirements.  \n" +
                "   - ⚠\uFE0F **Critical Dates**: Effective dates, deadlines, renewal terms.  \n" +
                "   - \uD83D\uDD0D **Notable Clauses**: Highlight 2-3 most consequential sections.  \n" +
                "\n" +
                "4. **Risk & Compliance**  \n" +
                "   - **Confidentiality**: Scope (if any).  \n" +
                "   - **Penalties/Enforcement**: Non-compliance consequences.  \n" +
                "\n" +
                "### **Adaptive Rules**  \n" +
                "- If the document lacks a section (e.g., no jurisdiction), omit it.  \n" +
                "- Use **bold** headers, `•` for lists. Max **300 words**.  \n" +
                "\n" +
                "**Document Text**:  " + extractedText;

    }
    private String createRiskClausePrompt(String extractedText) {
        return  "Identify and explain potential risks in the uploaded legal/business document.  \n" +
                "\n" +
                "### **Risk Findings**  \n" +
                "**1. Risk Type**: [Financial / Legal / Operational / Compliance]  \n" +
                "- **Location**: §3.2 (or \"Page 4, Paragraph 2\")  \n" +
                "- **Excerpt**:  \n" +
                "  > \"Party A assumes all liability for third-party claims.\"  \n" +
                "- **Severity**: \uD83D\uDD34 High (Unlimited liability exposes Party A to significant financial risk.)  \n" +
                "- **Suggested Mitigation**:  \n" +
                "  - \"Cap liability at [X%] of contract value.\"  \n" +
                "\n" +
                "**2. Risk Type**: [Compliance]  \n" +
                "- **Location**: Section 5.1  \n" +
                "- **Excerpt**:  \n" +
                "  > \"Data may be stored in any country.\"  \n" +
                "- **Severity**: \uD83D\uDFE0 Medium (Violates GDPR territorial restrictions.)  \n" +
                "- **Suggested Mitigation**:  \n" +
                "  - \"Specify GDPR-compliant storage locations (e.g., EU servers).\"  \n" +
                "\n" +
                "### **Detection Logic**  \n" +
                "- Flagged risks include:  \n" +
                "  - ✅ **Ambiguous terms**: \"Reasonable efforts,\" \"as applicable.\"  \n" +
                "  - ✅ **One-sided clauses**: Unconditional indemnification, excessive penalties.  \n" +
                "  - ✅ **Compliance gaps**: Conflicts with GDPR, HIPAA, or industry standards.  \n" +
                "\n" +
                "**Document Text**:" + extractedText;
    }
    private String createConfidentialityClausePrompt(String extractedText) {
        return "**Confidentiality Tracker Report**\n\n" +
                "Analyze the contract and find all confidentiality-related clauses.\n\n" +
                "### Output Format:\n" +
                "- **Clause Number**: [If available]\n" +
                "- **Clause Text**: [Full text mentioning confidentiality]\n" +
                "- **Type**: [Unilateral / Mutual / Non-standard]\n" +
                "- **Scope**: [What is considered confidential?]\n" +
                "- **Term Duration**: [Time period confidentiality remains in force]\n\n" +
                "**Detection Guidelines**:\n" +
                "- Include terms like 'non-disclosure', 'confidential', 'proprietary information'\n" +
                "- Ignore general definitions unless legally significant\n\n" +
                "**Document to Analyze**:\n" + extractedText;
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
                "**Document to Process**:\n" + extractedText;
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
                "**Contract**:\n" + extractedText;
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
                "- Contract Text Provided:\n" + extractedText;
    }
}
