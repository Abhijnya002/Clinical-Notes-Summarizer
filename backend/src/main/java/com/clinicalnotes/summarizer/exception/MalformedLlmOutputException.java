package com.clinicalnotes.summarizer.exception;

public class MalformedLlmOutputException extends RuntimeException {

    private final String rawOutput;

    public MalformedLlmOutputException(String message, String rawOutput, Throwable cause) {
        super(message, cause);
        this.rawOutput = rawOutput;
    }

    public String rawOutput() {
        return rawOutput;
    }
}
