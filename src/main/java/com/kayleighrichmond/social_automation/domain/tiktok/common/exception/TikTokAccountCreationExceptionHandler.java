package com.kayleighrichmond.social_automation.domain.tiktok.common.exception;

import com.kayleighrichmond.social_automation.common.exception.BrowserCaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.proxy.model.Proxy;
import com.kayleighrichmond.social_automation.domain.proxy.service.ProxyService;
import com.kayleighrichmond.social_automation.domain.proxy.web.dto.UpdateProxyRequest;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
import com.kayleighrichmond.social_automation.system.client.nst.exception.NstBrowserException;
import com.microsoft.playwright.PlaywrightException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
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

        if (e instanceof NstBrowserException) {
            handleNstBrowserException((NstBrowserException) e);
            return;
        }

        if (e instanceof SendCodeButtonNotWorkedException) {
            handleSendCodeButtonNotWorkedException(tikTokAccount, (SendCodeButtonNotWorkedException) e);
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
            tikTokAccountsInProgress.setExecutionMessage("Something went wrong while getting access to DOM elements. Probably bad internet connection");
        }
        tikTokService.saveAll(allTikTokAccountsInProgress);

        throw new ServerException("Something went wrong while getting access to DOM elements. Probably bad internet connection");
    }

    private void handleCaptchaException(TikTokAccount tikTokAccount) {
        log.warn("Captcha appeared on Tik Tok account {}. Trying to rotate proxy id and skipping account creation...", tikTokAccount.getEmail());

        String executionMessage = "Captcha appearance.";
        Proxy proxy = tikTokAccount.getProxy();

        boolean hasRotated = proxyHelper.rotateProxy(proxy);
        if (!hasRotated) {
            proxyService.update(proxy.getId(), UpdateProxyRequest.builder().verified(false).build());
            long proxyRotationTimeout = proxy.getAutoRotateInterval() - (Instant.now().getEpochSecond() - proxy.getLastRotation().getEpochSecond());
            executionMessage += " Proxy rotation failed. Wait till this proxy rotate automatically in " + proxyRotationTimeout / 1000 + " seconds";
        } else {
            proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).lastRotation(Instant.now()).build());
            executionMessage += " Proxy rotated successfully. Try again.";
        }

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.FAILED)
                .executionMessage(executionMessage)
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    private void handleBrowserCaptchaException(TikTokAccount tikTokAccount) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.FAILED)
                .executionMessage("Browser captcha appearance")
                .build();

        log.warn("Browser captcha appeared before entering to Tik Tok. Stopping... retry again");

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    private void handleNstBrowserException(NstBrowserException e) {
        log.error(e.getMessage());

        List<TikTokAccount> allTikTokAccountsInProgress = tikTokService.findAllByStatus(Status.IN_PROGRESS);
        for (TikTokAccount tikTokAccountsInProgress : allTikTokAccountsInProgress) {
            tikTokAccountsInProgress.setStatus(Status.FAILED);
            tikTokAccountsInProgress.setExecutionMessage(e.getMessage());
        }
        tikTokService.saveAll(allTikTokAccountsInProgress);

        throw new ServerException(e.getMessage());
    }

    private void handleSendCodeButtonNotWorkedException( TikTokAccount tikTokAccount, SendCodeButtonNotWorkedException e) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.FAILED)
                .executionMessage(e.getMessage())
                .build();

        log.warn("Sending code to email took to long. It may cause code expiration");

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }
}
