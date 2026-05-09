package com.clinicalnotes.summarizer.service;

import com.clinicalnotes.summarizer.config.LlmProperties;
import com.clinicalnotes.summarizer.dto.ClinicalSummary;
import com.clinicalnotes.summarizer.exception.LlmUnavailableException;
import com.clinicalnotes.summarizer.exception.MalformedLlmOutputException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Talks to a local Ollama instance (https://ollama.com) to run an
 * open-source model (default: llama3.2). Ollama's {@code format: "json"}
 * option is used to nudge the model toward returning valid JSON, which is
 * then strictly parsed and validated by {@link SummaryValidator} rather than
 * trusted blindly.
 *
 * Only the note length and timing are ever logged here -- never the note
 * content or the model's raw output, to avoid leaking PHI into application
 * logs.
 */
@Service
public class OllamaClientService {

    private static final Logger log = LoggerFactory.getLogger(OllamaClientService.class);

    private final RestClient ollamaRestClient;
    private final PromptBuilder promptBuilder;
    private final ObjectMapper objectMapper;
    private final LlmProperties llmProperties;

    public OllamaClientService(RestClient ollamaRestClient,
                                PromptBuilder promptBuilder,
                                ObjectMapper objectMapper,
                                LlmProperties llmProperties) {
        this.ollamaRestClient = ollamaRestClient;
        this.promptBuilder = promptBuilder;
        this.objectMapper = objectMapper;
        this.llmProperties = llmProperties;
    }

    public ClinicalSummary summarize(String clinicalNote) {
        String prompt = promptBuilder.buildSummarizationPrompt(clinicalNote);
        OllamaGenerateRequest request = new OllamaGenerateRequest(
                llmProperties.model(), prompt, "json", false);

        OllamaGenerateResponse response;
        try {
            response = ollamaRestClient.post()
                    .uri("/api/generate")
                    .body(request)
                    .retrieve()
                    .body(OllamaGenerateResponse.class);
        } catch (RestClientException e) {
            log.warn("Ollama request failed (noteLength={}): {}", clinicalNote.length(), e.getMessage());
            throw new LlmUnavailableException(
                    "Could not reach the local Ollama instance at " + llmProperties.baseUrl()
                            + ". Is `ollama serve` running and is the model pulled?", e);
        }

        if (response == null || response.response() == null || response.response().isBlank()) {
            throw new MalformedLlmOutputException("LLM returned an empty response", "", null);
        }

        try {
            return objectMapper.readValue(response.response(), ClinicalSummary.class);
        } catch (Exception e) {
            log.warn("Failed to parse LLM output as structured JSON (noteLength={})", clinicalNote.length());
            throw new MalformedLlmOutputException(
                    "LLM output could not be parsed as the expected JSON structure",
                    response.response(), e);
        }
    }

    private record OllamaGenerateRequest(String model, String prompt, String format, boolean stream) {
    }

    private record OllamaGenerateResponse(String response, boolean done) {
    }
}
