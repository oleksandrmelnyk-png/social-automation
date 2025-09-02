package com.kayleighrichmond.social_automation.domain.tiktok.common.exception;

import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.nst.exception.NstBrowserException;
import com.microsoft.playwright.PlaywrightException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

        if (e instanceof CaptchaException) {
            handleCaptchaException(tikTokAccount);
            return;
        }

        if (e instanceof NstBrowserException) {
            handleNstBrowserException(tikTokAccount.getId(),  (NstBrowserException) e);
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
                .executionMessage("Something went wrong while getting access to DOM elements")
                .build();

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);

        throw new ServerException("Something went wrong while getting access to DOM elements");
    }

    private void handleCaptchaException(TikTokAccount tikTokAccount) {
        log.warn("Captcha appeared on Tik Tok account {}", tikTokAccount.getEmail());

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.FAILED)
                .executionMessage("Captcha appearance")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    private void handleNstBrowserException(String accountId, NstBrowserException e) {
        log.error(e.getMessage());

        TikTokAccount tikTokAccount = tikTokService.findById(accountId);

        List<TikTokAccount> allTikTokAccountsInProgress = tikTokService.findAllByAction(tikTokAccount.getAction());
        for (TikTokAccount tikTokAccountsInProgress : allTikTokAccountsInProgress) {
            tikTokAccountsInProgress.setAction(Action.FAILED);
            tikTokAccountsInProgress.setExecutionMessage(e.getMessage());
        }
        tikTokService.saveAll(allTikTokAccountsInProgress);

        throw new ServerException("Nst browser exception occurred");
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        if (!clazz.equals(TikTokAccount.class)) {
            throw new IllegalArgumentException("TikTokAccount required");
        }
    }

}
