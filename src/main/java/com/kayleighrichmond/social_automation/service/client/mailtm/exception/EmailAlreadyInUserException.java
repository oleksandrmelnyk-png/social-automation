package com.kayleighrichmond.social_automation.service.client.mailtm.exception;

public class EmailAlreadyInUserException extends RuntimeException {
    public EmailAlreadyInUserException(String message) {
        super(message);
    }
}
