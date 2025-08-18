package com.kayleighrichmond.social_automation.service.api.account;

import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.web.dto.tiktok.CreateAccountsRequest;

public interface AccountCreator {

    void processAccountCreation(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();

}
