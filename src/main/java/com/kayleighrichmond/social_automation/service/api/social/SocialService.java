package com.kayleighrichmond.social_automation.service.api.social;

import com.kayleighrichmond.social_automation.service.api.account.AccountCreator;
import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final SocialRegistry socialRegistry;

    public void processAccountsCreation(CreateAccountsRequest createAccountsRequest) {
        AccountCreator accountCreator = socialRegistry.getAccountCreator(Platform.TIKTOK);
        accountCreator.processAccountCreation(createAccountsRequest);
    }

}
