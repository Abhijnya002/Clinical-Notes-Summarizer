package com.clinicalnotes.summarizer.controller;

import com.clinicalnotes.summarizer.dto.SummarizeRequest;
import com.clinicalnotes.summarizer.dto.SummarizeResponse;
import com.clinicalnotes.summarizer.service.SummarizeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SummarizeController {

    private final SummarizeService summarizeService;

    public SummarizeController(SummarizeService summarizeService) {
        this.summarizeService = summarizeService;
    }

    @PostMapping("/api/summarize")
    public SummarizeResponse summarize(@Valid @RequestBody SummarizeRequest request) {
        return summarizeService.summarize(request.clinicalNote());
    }
}
