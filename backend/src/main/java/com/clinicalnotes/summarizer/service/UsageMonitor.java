package com.clinicalnotes.summarizer.service;

import com.clinicalnotes.summarizer.dto.UsageStats;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory, process-local usage counters. Deliberately tracks only counts
 * and timings -- never clinical note content or model output -- so this
 * component carries no PHI exposure risk. For a multi-instance production
 * deployment, back this with Micrometer + a metrics backend (e.g.
 * CloudWatch) instead of local memory.
 */
@Component
public class UsageMonitor {

    private final AtomicLong totalRequests = new AtomicLong();
    private final AtomicLong successfulRequests = new AtomicLong();
    private final AtomicLong failedRequests = new AtomicLong();
    private final AtomicLong totalLatencyMs = new AtomicLong();
    private final AtomicLong lastRequestEpochMillis = new AtomicLong(-1);

    public void recordSuccess(long latencyMs) {
        totalRequests.incrementAndGet();
        successfulRequests.incrementAndGet();
        totalLatencyMs.addAndGet(latencyMs);
        lastRequestEpochMillis.set(System.currentTimeMillis());
    }

    public void recordFailure(long latencyMs) {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
        totalLatencyMs.addAndGet(latencyMs);
        lastRequestEpochMillis.set(System.currentTimeMillis());
    }

    public UsageStats snapshot() {
        long total = totalRequests.get();
        double avgLatency = total == 0 ? 0.0 : (double) totalLatencyMs.get() / total;
        long last = lastRequestEpochMillis.get();

        return new UsageStats(
                total,
                successfulRequests.get(),
                failedRequests.get(),
                avgLatency,
                last < 0 ? null : last
        );
    }
}
