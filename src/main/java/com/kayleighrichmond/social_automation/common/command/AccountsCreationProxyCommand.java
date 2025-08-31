package com.kayleighrichmond.social_automation.common.command;

import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.system.controller.dto.CreateAccountsRequest;

public interface AccountsCreationProxyCommand {

    void executeAvailableProxies(CreateAccountsRequest createAccountsRequest);

    Platform getPlatform();
}
