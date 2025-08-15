package com.kayleighrichmond.social_automation.service.account.exception;

public class AccountsCurrentlyCreatingException extends RuntimeException {
    public AccountsCurrentlyCreatingException(String message) {
        super(message);
    }
}
