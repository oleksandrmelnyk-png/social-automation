package com.kayleighrichmond.social_automation.common.registry;

import com.kayleighrichmond.social_automation.common.ActionHandler;
import com.kayleighrichmond.social_automation.common.type.Action;
import com.kayleighrichmond.social_automation.common.type.Platform;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ActionHandlerRegistry {

    private final Map<Platform, Map<Action, ActionHandler>> actionsByPlatform;

    public ActionHandlerRegistry(Set<ActionHandler> actionHandlers) {
        this.actionsByPlatform = actionHandlers.stream()
                .collect(Collectors.groupingBy(
                        ActionHandler::getPlatform,
                        Collectors.toMap(ActionHandler::getAction, h -> h)
                ));
    }

    public ActionHandler getActionHandler(Platform platform, Action action) {
        return Optional.ofNullable(actionsByPlatform.get(platform))
                .map(m -> m.get(action))
                .orElseThrow(() ->
                        new IllegalStateException("No handler exists for platform: " + platform + " and action: " + action));
    }
}
