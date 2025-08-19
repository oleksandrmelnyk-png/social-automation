package com.kayleighrichmond.social_automation.service.api.account;

import com.kayleighrichmond.social_automation.domain.type.Platform;
import com.kayleighrichmond.social_automation.web.controller.social.dto.CreateAccountsRequest;

public interface AccountCreator {

    void processAccountCreation(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();

}
