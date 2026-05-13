package com.clinicalnotes.summarizer.dto;

import java.util.List;

/**
 * Response returned to the client. {@code rawModelOutput} is only populated
 * when validation fails, to aid debugging without persisting it anywhere.
 */
public record SummarizeResponse(
        ClinicalSummary summary,
        boolean validated,
        List<String> validationWarnings,
        String rawModelOutput
) {
}
