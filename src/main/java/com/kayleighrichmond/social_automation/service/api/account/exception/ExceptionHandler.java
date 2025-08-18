package com.kayleighrichmond.social_automation.service.api.account.exception;

import com.kayleighrichmond.social_automation.domain.entity.account.BaseEntity;

public interface ExceptionHandler {

    void handle(Exception e, BaseEntity entity);

    void handleDefault(Exception e);

    void verifyArgument(Class<?> clazz) throws IllegalArgumentException;

}
