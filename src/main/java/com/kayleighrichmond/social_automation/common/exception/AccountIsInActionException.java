package com.kayleighrichmond.social_automation.common.exception;

public class AccountIsInActionException extends RuntimeException {
    public AccountIsInActionException(String message) {
        super(message);
    }
}
