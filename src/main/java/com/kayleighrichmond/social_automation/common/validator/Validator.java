package com.kayleighrichmond.social_automation.common.validator;

public interface Validator {

    void validate(Object target);

    void verifyArgument(Class<?> clazz) throws IllegalArgumentException;

}
