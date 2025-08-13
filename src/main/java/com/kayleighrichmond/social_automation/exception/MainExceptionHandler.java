package com.kayleighrichmond.social_automation.exception;

import com.kayleighrichmond.social_automation.service.proxy.exception.NoProxiesAvailableException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MainExceptionHandler {

    @ExceptionHandler(NoProxiesAvailableException.class)
    public ResponseEntity<String> handleNoProxiesAvailableException(NoProxiesAvailableException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(404))
                .body(e.getMessage());
    }

    @ExceptionHandler(AccountsCurrentlyCreatingException.class)
    public ResponseEntity<String> handleAccountsCurrentlyCreatingException(AccountsCurrentlyCreatingException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(e.getMessage());
    }

}
