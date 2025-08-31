package com.kayleighrichmond.social_automation.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.NotEnoughProxiesException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyAlreadyExistsException;
import com.kayleighrichmond.social_automation.system.client.ip_api.IpApiException;
import com.kayleighrichmond.social_automation.system.client.mailtm.exception.MailTmApiException;
import com.kayleighrichmond.social_automation.system.client.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.NoProxiesAvailableException;
import com.kayleighrichmond.social_automation.domain.proxy.common.exception.ProxyNotVerifiedException;
import com.kayleighrichmond.social_automation.system.client.randomuser.RandomUserApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    @ExceptionHandler(AccountIsInActionException.class)
    public ResponseEntity<String> handleAccountIsInActionException(AccountIsInActionException e) {
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
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<String> handleAccountNotFoundException(AccountNotFoundException e) {
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        String invalidFields = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(invalidFields);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {

        String responseMessage = "Invalid request payload";
        Throwable throwable = e.getCause();

        if (throwable instanceof InvalidFormatException invalidFormatException) {
            Class<?> targetType = invalidFormatException.getTargetType();
            List<?> acceptedValues = targetType.isEnum()
                    ? Arrays.asList(targetType.getEnumConstants())
                    : List.of();

            if (!acceptedValues.isEmpty()) {
                responseMessage = "Accepted values are only: " + acceptedValues;
            }
        }

        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body(responseMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {

        log.error("IllegalArgumentException: ", e);

        return ResponseEntity
                .status(HttpStatusCode.valueOf(400))
                .body("Wrong argument passed");
    }

}
