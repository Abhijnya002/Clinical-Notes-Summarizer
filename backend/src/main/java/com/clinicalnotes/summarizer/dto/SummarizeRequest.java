package com.clinicalnotes.summarizer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Incoming request containing a de-identified clinical note to summarize.
 * Callers are responsible for stripping direct patient identifiers before
 * sending text to this API (see README HIPAA notes).
 */
public record SummarizeRequest(
        @NotBlank(message = "clinicalNote must not be blank")
        @Size(max = 20_000, message = "clinicalNote must be under 20,000 characters")
        String clinicalNote
) {
}
