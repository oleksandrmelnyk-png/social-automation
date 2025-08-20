package com.kayleighrichmond.social_automation.service.api.account.tiktok.exception;

import com.kayleighrichmond.social_automation.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.service.api.account.exception.CaptchaException;
import com.kayleighrichmond.social_automation.service.api.account.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.service.api.proxy.ProxyHelper;
import com.kayleighrichmond.social_automation.domain.type.Status;
import com.kayleighrichmond.social_automation.web.controller.tiktok.dto.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokAccountCreationExceptionHandler implements ExceptionHandler {

    private final ProxyHelper proxyHelper;

    private final TikTokService tikTokService;

    public void handle(Exception e, Object target) {
        verifyArgument(target.getClass());
        TikTokAccount tikTokAccount = (TikTokAccount) target;

        if (e instanceof CaptchaException) {
            handleCaptchaException(tikTokAccount);
            return;
        }

        handleDefault(e);
    }

    @Override
    public void handleDefault(Exception e) {
        log.error("Default handler: {}", e.getMessage());
        tikTokService.updateAllFromInProgressToFailed("Unexpected server exception");
        throw new ServerException(e.getMessage());
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        if (!clazz.equals(TikTokAccount.class)) {
            throw new IllegalArgumentException("TikTokAccount required");
        }
    }

    private void handleCaptchaException(TikTokAccount tikTokAccount) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .status(Status.FAILED)
                .executionMessage("Captcha appearance")
                .build();

        log.warn("Captcha appeared on Tik Tok account {}. Rotating proxy id and skipping account creation...", tikTokAccount.getEmail());

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
        proxyHelper.rotateProxyAndResetAccountsLinked(tikTokAccount.getProxy());
    }
}
