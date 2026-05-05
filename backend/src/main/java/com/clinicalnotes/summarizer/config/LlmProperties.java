package com.clinicalnotes.summarizer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "llm.ollama")
public record LlmProperties(String baseUrl, String model, int timeoutSeconds) {
}
