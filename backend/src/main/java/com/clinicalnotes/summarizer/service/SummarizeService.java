package com.clinicalnotes.summarizer.service;

import com.clinicalnotes.summarizer.dto.ClinicalSummary;
import com.clinicalnotes.summarizer.dto.SummarizeResponse;
import com.clinicalnotes.summarizer.exception.MalformedLlmOutputException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SummarizeService {

    private static final Logger log = LoggerFactory.getLogger(SummarizeService.class);

    private final OllamaClientService ollamaClientService;
    private final SummaryValidator summaryValidator;
    private final UsageMonitor usageMonitor;

    public SummarizeService(OllamaClientService ollamaClientService,
                             SummaryValidator summaryValidator,
                             UsageMonitor usageMonitor) {
        this.ollamaClientService = ollamaClientService;
        this.summaryValidator = summaryValidator;
        this.usageMonitor = usageMonitor;
    }

    public SummarizeResponse summarize(String clinicalNote) {
        long start = System.currentTimeMillis();
        try {
            ClinicalSummary summary = ollamaClientService.summarize(clinicalNote);
            SummaryValidator.ValidationResult validation = summaryValidator.validate(summary);

            usageMonitor.recordSuccess(System.currentTimeMillis() - start);
            log.info("Summarized note (length={}, validated={}, warnings={})",
                    clinicalNote.length(), validation.validated(), validation.warnings().size());

            return new SummarizeResponse(
                    summary,
                    validation.validated(),
                    validation.warnings(),
                    validation.validated() ? null : "See warnings; raw model output withheld from API response by default"
            );
        } catch (MalformedLlmOutputException e) {
            usageMonitor.recordFailure(System.currentTimeMillis() - start);
            log.warn("Malformed LLM output for note (length={}): {}", clinicalNote.length(), e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            usageMonitor.recordFailure(System.currentTimeMillis() - start);
            throw e;
        }
    }
}
