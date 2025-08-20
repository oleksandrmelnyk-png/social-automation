package com.kayleighrichmond.social_automation.common;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;

public interface AccountCreator {

    void processAccountCreation(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();

}
