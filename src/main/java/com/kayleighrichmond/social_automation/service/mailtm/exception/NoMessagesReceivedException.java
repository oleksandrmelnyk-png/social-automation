package com.kayleighrichmond.social_automation.service.mailtm.exception;

public class NoMessagesReceivedException extends RuntimeException {
    public NoMessagesReceivedException(String message) {
        super(message);
    }
}
