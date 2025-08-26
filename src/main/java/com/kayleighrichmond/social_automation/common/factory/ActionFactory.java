package com.kayleighrichmond.social_automation.common.factory;

import com.kayleighrichmond.social_automation.common.command.ActionCommand;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ActionFactory {

    private final Map<Platform, Map<Action, ActionCommand>> actionCommandsByPlatform;

    public ActionFactory(Set<ActionCommand> actionCommands) {
        this.actionCommandsByPlatform = actionCommands.stream()
                .collect(Collectors.groupingBy(
                        ActionCommand::getPlatform,
                        Collectors.toMap(ActionCommand::getAction, h -> h)
                ));
    }

    public ActionCommand getActionCommand(Platform platform, Action action) {
        return Optional.ofNullable(actionCommandsByPlatform.get(platform))
                .map(m -> m.get(action))
                .orElseThrow(() ->
                        new IllegalStateException("No action commands exists by platform: " + platform + " and action: " + action));
    }
}
