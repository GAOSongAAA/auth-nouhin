package com.collaboportal.common.strategy.auth;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthenticationStrategyRegistry {

    private final Map<String, AuthenticationStrategy> strategies = new ConcurrentHashMap<>();

    public AuthenticationStrategyRegistry(Map<String, AuthenticationStrategy> strategies) {
        this.strategies.putAll(strategies);
    }

    public AuthenticationStrategy getStrategy(String type) {
        return strategies.get(type);
    }
}
