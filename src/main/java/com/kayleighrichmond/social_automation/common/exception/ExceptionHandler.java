package com.kayleighrichmond.social_automation.common.exception;

public interface ExceptionHandler {

    void handle(Throwable e, Object target);

    void verifyArgument(Class<?> clazz) throws IllegalArgumentException;

}
