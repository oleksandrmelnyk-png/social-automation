package com.kayleighrichmond.social_automation.domain.proxy.common.exception;

public class ProxyRotationFailed extends RuntimeException {
    public ProxyRotationFailed(String message) {
        super(message);
    }
}
