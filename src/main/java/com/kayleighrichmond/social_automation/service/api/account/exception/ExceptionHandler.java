package com.kayleighrichmond.social_automation.service.api.account.exception;

public interface ExceptionHandler {

    void handle(Exception e, Object target);

    void handleDefault(Exception e);

    void verifyArgument(Class<?> clazz) throws IllegalArgumentException;

}
