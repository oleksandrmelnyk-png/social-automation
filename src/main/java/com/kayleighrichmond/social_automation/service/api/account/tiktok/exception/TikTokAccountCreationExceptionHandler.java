package com.kayleighrichmond.social_automation.service.api.account.tiktok.exception;

import com.kayleighrichmond.social_automation.domain.entity.account.BaseEntity;
import com.kayleighrichmond.social_automation.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.entity.account.TikTokAccount;
import com.kayleighrichmond.social_automation.service.api.account.exception.CaptchaException;
import com.kayleighrichmond.social_automation.service.api.account.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.service.api.account.tiktok.TikTokService;
import com.kayleighrichmond.social_automation.service.client.nst.exception.NstBrowserException;
import com.kayleighrichmond.social_automation.service.api.proxy.ProxyHelper;
import com.kayleighrichmond.social_automation.domain.type.Status;
import com.kayleighrichmond.social_automation.web.dto.tiktok.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokAccountCreationExceptionHandler implements ExceptionHandler {

    private final TikTokService tikTokService;

    private final ProxyHelper proxyHelper;

    public void handle(Exception e, BaseEntity baseEntity) {
        verifyArgument(baseEntity.getClass());
        TikTokAccount tikTokAccount = (TikTokAccount) baseEntity;

        if (e instanceof CaptchaException) {
            handleCaptchaException(tikTokAccount);
            return;
        }

        if (e instanceof NstBrowserException) {
            handleNstBrowserException(e);
            return;
        }

        handleDefault(e);
    }

    @Override
    public void handleDefault(Exception e) {
        log.error("Default handler: {}", e.getMessage());
        tikTokService.updateAllFromInProgressToFailed(e.getMessage());
        throw new ServerException(e.getMessage());
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        if (!clazz.equals(TikTokAccount.class)) {
            throw new IllegalArgumentException("TikTokAccount required");
        }
    }

    private void handleNstBrowserException(Exception e) {
        log.error("Exception: {}", e.getMessage());
        tikTokService.updateAllFromInProgressToFailed(e.getMessage());
        throw new ServerException(e.getMessage());
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
