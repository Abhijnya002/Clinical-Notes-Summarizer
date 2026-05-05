package com.clinicalnotes.summarizer;

import com.clinicalnotes.summarizer.config.LlmProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LlmProperties.class)
public class ClinicalNotesSummarizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClinicalNotesSummarizerApplication.class, args);
    }
}
