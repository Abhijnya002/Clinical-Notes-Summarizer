package com.clinicalnotes.summarizer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Structured summary the LLM is prompted to produce. Field names here must
 * match the JSON keys requested in {@code PromptBuilder} exactly.
 */
public record ClinicalSummary(
        @JsonProperty("chiefComplaint") String chiefComplaint,
        @JsonProperty("historyOfPresentIllness") String historyOfPresentIllness,
        @JsonProperty("assessment") String assessment,
        @JsonProperty("plan") String plan,
        @JsonProperty("medications") List<String> medications,
        @JsonProperty("followUp") String followUp
) {
    @JsonCreator
    public ClinicalSummary {
        medications = medications == null ? List.of() : medications;
    }
}
