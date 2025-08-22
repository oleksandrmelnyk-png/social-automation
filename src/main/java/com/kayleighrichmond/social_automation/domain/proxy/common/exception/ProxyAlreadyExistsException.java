package com.kayleighrichmond.social_automation.domain.proxy.common.exception;

public class ProxyAlreadyExistsException extends RuntimeException {
    public ProxyAlreadyExistsException(String message) {
        super(message);
    }
}
