package com.kayleighrichmond.social_automation.service.account;

import com.kayleighrichmond.social_automation.type.Platform;
import com.kayleighrichmond.social_automation.web.dto.account.CreateAccountsRequest;

public interface AccountCreator {

    void processAccountCreation(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();

}
