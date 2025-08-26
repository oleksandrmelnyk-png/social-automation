package com.kayleighrichmond.social_automation.common.command;

import com.kayleighrichmond.social_automation.common.type.Platform;

public interface ProxyCommand {

    void executeActiveProxy(String accountId);

    Platform getPlatform();

}
