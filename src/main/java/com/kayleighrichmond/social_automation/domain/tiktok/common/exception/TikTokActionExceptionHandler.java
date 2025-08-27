package com.kayleighrichmond.social_automation.domain.tiktok.common.exception;

import com.kayleighrichmond.social_automation.common.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.microsoft.playwright.PlaywrightException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokActionExceptionHandler implements ExceptionHandler {

    private final TikTokService tikTokService;

    @Override
    public void handle(Throwable e, Object target) {
        verifyArgument(target.getClass());
        TikTokAccount tikTokAccount = (TikTokAccount) target;

        if (e instanceof PlaywrightException) {
            handlePlaywrightException((PlaywrightException) e, tikTokAccount);
            return;
        }

        handleDefault(e, tikTokAccount);
    }

    public void handleDefault(Throwable e, TikTokAccount tikTokAccount) {
        log.error(e.getMessage());

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.FAILED)
                .executionMessage("Unexpected server exception")
                .build();

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);

        throw new ServerException("Something went wrong while processing action");
    }

    private void handlePlaywrightException(PlaywrightException e, TikTokAccount tikTokAccount) {
        log.error(e.getMessage());

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.FAILED)
                .executionMessage("Something went wrong while getting access to DOM elements. Probably bad network connection")
                .build();

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);

        throw new ServerException("Something went wrong while getting access to DOM elements. Probably bad network connection");
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        if (!clazz.equals(TikTokAccount.class)) {
            throw new IllegalArgumentException("TikTokAccount required");
        }
    }

}
