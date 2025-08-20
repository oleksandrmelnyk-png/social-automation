package com.kayleighrichmond.social_automation.system.controller.validator;

import com.kayleighrichmond.social_automation.common.exception.AccountIsInActionException;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.validator.Validator;
import com.kayleighrichmond.social_automation.domain.tiktok.model.TikTokAccount;
import com.kayleighrichmond.social_automation.domain.tiktok.service.TikTokService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialAccountLikeValidator implements Validator {

    private final TikTokService tikTokService;

    @Override
    public void validate(Object target) {
        verifyArgument(target.getClass());
        String accountId = (String) target;

        TikTokAccount tikTokAccount = tikTokService.findById(accountId);
        if (tikTokAccount.getAction() != Action.ACTED && tikTokAccount.getAction() != Action.FAILED && tikTokAccount.getAction() != null) {
            throw new AccountIsInActionException("This account is already in action");
        }
    }

    @Override
    public void verifyArgument(Class<?> clazz) throws IllegalArgumentException {
        boolean verified = clazz.equals(String.class);
        if (!verified) {
            throw new IllegalArgumentException("String required");
        }
    }
}
