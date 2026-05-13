package com.clinicalnotes.summarizer.service;

import com.clinicalnotes.summarizer.dto.ClinicalSummary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates that the structured output produced by the LLM actually
 * contains the fields a clinical summary needs before it's shown to a user.
 * The LLM is untrusted here -- a syntactically valid JSON object with blank
 * or missing clinical fields is still flagged as unvalidated.
 */
@Component
public class SummaryValidator {

    private static final String NOT_DOCUMENTED = "Not documented";

    public ValidationResult validate(ClinicalSummary summary) {
        List<String> warnings = new ArrayList<>();

        requireNonBlank(summary.chiefComplaint(), "chiefComplaint", warnings);
        requireNonBlank(summary.assessment(), "assessment", warnings);
        requireNonBlank(summary.plan(), "plan", warnings);

        if (isBlank(summary.historyOfPresentIllness())) {
            warnings.add("historyOfPresentIllness was not documented by the model");
        }
        if (isBlank(summary.followUp())) {
            warnings.add("followUp was not documented by the model");
        }

        boolean validated = warnings.isEmpty()
                || warnings.stream().noneMatch(this::isBlockingWarning);

        return new ValidationResult(validated, warnings);
    }

    private void requireNonBlank(String value, String fieldName, List<String> warnings) {
        if (isBlank(value) || NOT_DOCUMENTED.equalsIgnoreCase(value.trim())) {
            warnings.add(fieldName + " is missing a required clinical value");
        }
    }

    private boolean isBlockingWarning(String warning) {
        return warning.contains("required clinical value");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public record ValidationResult(boolean validated, List<String> warnings) {
    }
}
