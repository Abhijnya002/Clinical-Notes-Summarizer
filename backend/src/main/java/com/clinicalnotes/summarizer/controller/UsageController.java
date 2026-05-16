package com.clinicalnotes.summarizer.controller;

import com.clinicalnotes.summarizer.dto.UsageStats;
import com.clinicalnotes.summarizer.service.UsageMonitor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsageController {

    private final UsageMonitor usageMonitor;

    public UsageController(UsageMonitor usageMonitor) {
        this.usageMonitor = usageMonitor;
    }

    @GetMapping("/api/usage")
    public UsageStats usage() {
        return usageMonitor.snapshot();
    }
}
