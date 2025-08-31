package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.command.AccountActionCommand;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ActionActionFactory {

    private final Map<Platform, Map<Action, AccountActionCommand>> actionCommandsByPlatform;

    public ActionActionFactory(Set<AccountActionCommand> accountActionCommands) {
        this.actionCommandsByPlatform = accountActionCommands.stream()
                .collect(Collectors.groupingBy(
                        AccountActionCommand::getPlatform,
                        Collectors.toMap(AccountActionCommand::getAction, h -> h)
                ));
    }

    public AccountActionCommand getActionCommand(Platform platform, Action action) {
        return Optional.ofNullable(actionCommandsByPlatform.get(platform))
                .map(m -> m.get(action))
                .orElseThrow(() ->
                        new IllegalStateException("No action commands exists by platform: " + platform + " and action: " + action));
    }
}
