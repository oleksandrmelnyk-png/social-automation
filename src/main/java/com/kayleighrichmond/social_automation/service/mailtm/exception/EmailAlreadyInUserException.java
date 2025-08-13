package com.kayleighrichmond.social_automation.service.mailtm.exception;

public class EmailAlreadyInUserException extends RuntimeException {
    public EmailAlreadyInUserException(String message) {
        super(message);
    }
}
