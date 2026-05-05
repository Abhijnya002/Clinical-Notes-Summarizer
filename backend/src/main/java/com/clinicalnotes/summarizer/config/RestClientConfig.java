package com.clinicalnotes.summarizer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient ollamaRestClient(LlmProperties llmProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10_000);
        requestFactory.setReadTimeout(llmProperties.timeoutSeconds() * 1000);

        return RestClient.builder()
                .baseUrl(llmProperties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
