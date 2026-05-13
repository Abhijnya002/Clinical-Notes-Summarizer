package com.clinicalnotes.summarizer.service;

import com.clinicalnotes.summarizer.dto.ClinicalSummary;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SummaryValidatorTest {

    private final SummaryValidator validator = new SummaryValidator();

    @Test
    void validatesCompleteSummary() {
        ClinicalSummary summary = new ClinicalSummary(
                "Shortness of breath",
                "Patient reports 3 days of dyspnea on exertion",
                "Likely mild CHF exacerbation",
                "Start furosemide, follow up in 1 week",
                List.of("Furosemide 20mg daily"),
                "Return in 1 week or sooner if symptoms worsen"
        );

        SummaryValidator.ValidationResult result = validator.validate(summary);

        assertTrue(result.validated());
        assertTrue(result.warnings().isEmpty());
    }

    @Test
    void flagsMissingRequiredFields() {
        ClinicalSummary summary = new ClinicalSummary(
                "", "Not documented", null, "", List.of(), null
        );

        SummaryValidator.ValidationResult result = validator.validate(summary);

        assertFalse(result.validated());
        assertFalse(result.warnings().isEmpty());
    }
}
