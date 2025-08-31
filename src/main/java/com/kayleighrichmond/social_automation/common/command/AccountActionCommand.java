package com.kayleighrichmond.social_automation.common.command;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.system.controller.dto.ProcessActionRequest;

public interface AccountActionCommand {

    void executeAction(String accountId, ProcessActionRequest processActionRequest);

    Platform getPlatform();

    Action getAction();

}
