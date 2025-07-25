package com.collaboportal.common.spring.registry;

import com.collaboportal.common.strategy.auth.AuthenticationStrategy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthenticationStrategyRegistry {

    private final Map<String, AuthenticationStrategy> strategies = new ConcurrentHashMap<>();

    public AuthenticationStrategyRegistry(Map<String, AuthenticationStrategy> strategyMap) {
        this.strategies.putAll(strategyMap);
    }

    public AuthenticationStrategy getStrategy(String type) {
        return strategies.get(type + "AuthStrategy");
    }
}
