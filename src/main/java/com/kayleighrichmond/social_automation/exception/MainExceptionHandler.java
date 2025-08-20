package com.kayleighrichmond.social_automation.exception;

import com.kayleighrichmond.social_automation.service.api.account.exception.AccountsCurrentlyCreatingException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyAlreadyExistsException;
import com.kayleighrichmond.social_automation.service.client.ip_api.IpApiException;
import com.kayleighrichmond.social_automation.service.client.mailtm.exception.MailTmApiException;
import com.kayleighrichmond.social_automation.service.client.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.NoProxiesAvailableException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.service.api.proxy.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.service.client.randomuser.RandomUserApiException;
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

    @ExceptionHandler(ProxyNotVerifiedException.class)
    public ResponseEntity<String> handleProxyNotVerifiedException(ProxyNotVerifiedException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(e.getMessage());
    }

    @ExceptionHandler(ProxyAlreadyExistsException.class)
    public ResponseEntity<String> handleProxyAlreadyExistsException(ProxyAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(e.getMessage());
    }

    @ExceptionHandler(MailTmApiException.class)
    public ResponseEntity<String> handleMailTmApiException(MailTmApiException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }

    @ExceptionHandler(IpApiException.class)
    public ResponseEntity<String> handleIpApiException(IpApiException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }

    @ExceptionHandler(RandomUserApiException.class)
    public ResponseEntity<String> handleRandomUserApiException(RandomUserApiException e) {
        return ResponseEntity
                .status(HttpStatusCode.valueOf(500))
                .body(e.getMessage());
    }
}
