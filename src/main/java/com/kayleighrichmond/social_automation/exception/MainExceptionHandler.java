package com.kayleighrichmond.social_automation.exception;

import com.kayleighrichmond.social_automation.service.account.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.service.mailtm.exception.NoMessagesReceivedException;
import com.kayleighrichmond.social_automation.service.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.service.proxy.exception.NoProxiesAvailableException;
import com.kayleighrichmond.social_automation.service.proxy.exception.NotEnoughProxiesException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(NstBrowserException.class)
    public ResponseEntity<String> handleNstBrowserException(NstBrowserException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }

    @ExceptionHandler(NoMessagesReceivedException.class)
    public ResponseEntity<String> handleNoMessagesReceivedException(NoMessagesReceivedException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<String> handleServerException(ServerException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }

    @ExceptionHandler(NotEnoughProxiesException.class)
    public ResponseEntity<String> handleNotEnoughProxiesException(NotEnoughProxiesException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(e.getMessage());
    }
}
