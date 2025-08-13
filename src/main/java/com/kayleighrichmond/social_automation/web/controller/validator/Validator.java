package com.kayleighrichmond.social_automation.web.controller.validator;

public interface Validator {

    void validate(Object target);

    boolean verifyArgument(Class<?> clazz);

}
