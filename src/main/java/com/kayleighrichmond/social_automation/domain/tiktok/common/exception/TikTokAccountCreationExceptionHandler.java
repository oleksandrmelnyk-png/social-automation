package com.kayleighrichmond.social_automation.domain.tiktok.common.exception;

import com.kayleighrichmond.social_automation.common.exception.ServerException;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.common.exception.CaptchaException;
import com.kayleighrichmond.social_automation.common.exception.ExceptionHandler;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import com.kayleighrichmond.social_automation.common.helper.ProxyHelper;
import com.kayleighrichmond.social_automation.common.type.Status;
import com.kayleighrichmond.social_automation.domain.tiktok.web.dto.UpdateAccountRequest;
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
        tikTokService.updateAllFromCreationStatusInProgressToFailed("Unexpected server exception");
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
