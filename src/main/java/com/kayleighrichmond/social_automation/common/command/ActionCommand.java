package com.kayleighrichmond.social_automation.common.command;

import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import com.kayleighrichmond.social_automation.system.controller.dto.ActionRequest;

public interface ActionCommand {

    void executeAction(String accountId, ActionRequest actionRequest);

    Platform getPlatform();

    Action getAction();

}
