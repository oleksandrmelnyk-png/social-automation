package com.kayleighrichmond.social_automation.common.command;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;

public interface AccountCommand {

    void executeAccountCreation(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();

}
