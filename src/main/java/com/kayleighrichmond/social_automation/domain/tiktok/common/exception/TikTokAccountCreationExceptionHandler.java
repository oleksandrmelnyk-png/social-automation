package com.kayleighrichmond.social_automation.domain.tiktok.common.exception;

import com.kayleighrichmond.social_automation.common.exception.BrowserCaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyService;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.microsoft.playwright.PlaywrightException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokAccountCreationExceptionHandler implements ExceptionHandler {

    private final ProxyHelper proxyHelper;

    private final ProxyService proxyService;

    private final TikTokService tikTokService;

    public void handle(Throwable e, Object target) {
        verifyArgument(target.getClass());
        TikTokAccount tikTokAccount = (TikTokAccount) target;

        if (e instanceof PlaywrightException) {
            handlePlaywrightException((PlaywrightException) e);
            return;
        }

        if (e instanceof CaptchaException) {
            handleCaptchaException(tikTokAccount);
            return;
        }

        if (e instanceof BrowserCaptchaException) {
            handleBrowserCaptchaException(tikTokAccount);
            return;
        }

        handleDefault(e);
    }

    public void handleDefault(Throwable e) {
        log.error("Default handler: {}", e.getMessage());

        List<TikTokAccount> allTikTokAccountsInProgress = tikTokService.findAllByStatus(Status.IN_PROGRESS);
        for (TikTokAccount tikTokAccountsInProgress : allTikTokAccountsInProgress) {
            tikTokAccountsInProgress.setStatus(Status.FAILED);
            tikTokAccountsInProgress.setExecutionMessage("Unexpected server exception");
        }
        tikTokService.saveAll(allTikTokAccountsInProgress);

        throw new ServerException("Something went wrong while account creation");
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        if (!clazz.equals(TikTokAccount.class)) {
            throw new IllegalArgumentException("TikTokAccount required");
        }
    }

    private void handlePlaywrightException(PlaywrightException e) {
        log.error(e.getMessage());

        List<TikTokAccount> allTikTokAccountsInProgress = tikTokService.findAllByStatus(Status.IN_PROGRESS);
        for (TikTokAccount tikTokAccountsInProgress : allTikTokAccountsInProgress) {
            tikTokAccountsInProgress.setStatus(Status.FAILED);
            tikTokAccountsInProgress.setExecutionMessage("Something went wrong while getting access to DOM elements");
        }
        tikTokService.saveAll(allTikTokAccountsInProgress);

        throw new ServerException("Something went wrong while getting access to DOM elements");
    }

    private void handleCaptchaException(TikTokAccount tikTokAccount) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.FAILED)
                .executionMessage("Captcha appearance")
                .build();

        log.warn("Captcha appeared on Tik Tok account {}. Rotating proxy id and skipping account creation...", tikTokAccount.getEmail());

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
        proxyHelper.rotateProxy(tikTokAccount.getProxy());
        proxyService.update(tikTokAccount.getProxy().getId(), UpdateProxyRequest.builder().accountsLinked(0).build());
    }

    private void handleBrowserCaptchaException(TikTokAccount tikTokAccount) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.FAILED)
                .executionMessage("Browser captcha appearance")
                .build();

        log.warn("Browser captcha appeared before entering to Tik Tok. Stopping... retry again");

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }
}
