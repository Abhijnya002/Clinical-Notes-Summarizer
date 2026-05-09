package com.clinicalnotes.summarizer.service;

import org.springframework.stereotype.Component;

/**
 * Builds the prompt sent to the LLM. Keeps prompt engineering isolated from
 * transport concerns so wording can be iterated on without touching
 * {@link OllamaClientService}.
 */
@Component
public class PromptBuilder {

    public String buildSummarizationPrompt(String clinicalNote) {
        return """
                You are a clinical documentation assistant. Read the clinical note below
                and produce ONLY a JSON object (no prose, no markdown fences) with exactly
                these keys:

                - "chiefComplaint": string, the primary reason for the visit
                - "historyOfPresentIllness": string, a concise summary of the HPI
                - "assessment": string, the clinician's assessment/diagnosis
                - "plan": string, the treatment/management plan
                - "medications": array of strings, medications mentioned (empty array if none)
                - "followUp": string, follow-up instructions or timeframe

                Rules:
                - Do NOT invent facts that are not present in the note.
                - If a field cannot be determined from the note, use "Not documented".
                - Do not include any patient names, dates of birth, or other direct
                  identifiers in your output, even if present in the source note.
                - Output must be a single valid JSON object and nothing else.

                Clinical note:
                ---
                %s
                ---
                """.formatted(clinicalNote);
    }
}
