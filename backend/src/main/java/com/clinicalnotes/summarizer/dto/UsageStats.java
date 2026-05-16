package com.clinicalnotes.summarizer.dto;

/**
 * Aggregate, non-PHI usage counters exposed for basic operational monitoring.
 * No clinical note content or model output is ever tracked here.
 */
public record UsageStats(
        long totalRequests,
        long successfulRequests,
        long failedRequests,
        double averageLatencyMs,
        Long lastRequestEpochMillis
) {
}
