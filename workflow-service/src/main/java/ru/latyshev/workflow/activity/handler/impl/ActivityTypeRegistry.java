package ru.latyshev.workflow.activity.handler.impl;

import org.springframework.stereotype.Component;
import ru.latyshev.workflow.activity.handler.ActivityTypeHandler;
import ru.latyshev.workflow.enums.ActivityType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ActivityTypeRegistry {

    private final Map<ActivityType, ActivityTypeHandler> handlersByType;

    public ActivityTypeRegistry(List<ActivityTypeHandler> handlers) {
        this.handlersByType = handlers.stream()
                .collect(Collectors.toUnmodifiableMap(ActivityTypeHandler::type, Function.identity()));
    }

    public ActivityTypeHandler handler(ActivityType type) {
        ActivityTypeHandler handler = handlersByType.get(type);
        if (handler == null) {
            throw new IllegalArgumentException("No handler registered for activity type: " + type);
        }
        return handler;
    }
}
