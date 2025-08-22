package com.kayleighrichmond.social_automation.common.exception;

public interface ExceptionHandler {

    void handle(Exception e, Object target);

    void handleDefault(Exception e);

    void verifyArgument(Class<?> clazz) throws IllegalArgumentException;

}
