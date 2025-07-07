package com.collaboportal.common.jwt.logic.callbacklogin;

import com.collaboportal.common.strategy.AuthenticationStrategyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * JWT登錄策略註冊器（解耦版本）
 * 負責向全局策略註冊中心註冊基本的JWT登錄策略（test和prod）
 * 
 * 解耦改進：
 * 1. 不再繼承 LoginStrategyRegistry
 * 2. 使用全局策略註冊中心進行策略管理
 * 3. JWT 模塊獨立管理自己的策略
 */
@Component
public class JwtLoginStrategyRegistry {

    private static final Logger logger = LoggerFactory.getLogger(JwtLoginStrategyRegistry.class);

    private final AuthenticationStrategyRegistry globalStrategyRegistry;
    private final LoginStrategyInitializer loginStrategyInitializer;

    public JwtLoginStrategyRegistry(
            AuthenticationStrategyRegistry globalStrategyRegistry,
            LoginStrategyInitializer loginStrategyInitializer) {
        this.globalStrategyRegistry = globalStrategyRegistry;
        this.loginStrategyInitializer = loginStrategyInitializer;
    }

    @PostConstruct
    public void init() {
        logger.info("[JWT模塊] 開始向全局註冊中心註冊JWT登錄策略");

        // 註冊test策略
        globalStrategyRegistry.registerStrategy("test", loginStrategyInitializer.createTestStrategy());
        logger.info("[JWT模塊] 註冊test登錄策略成功");

        // 註冊基礎prod策略（可被OAuth2模塊擴展或覆蓋）
        globalStrategyRegistry.registerStrategy("prod", loginStrategyInitializer.createProdStrategy());
        logger.info("[JWT模塊] 註冊基礎prod登錄策略成功");

        logger.info("[JWT模塊] JWT登錄策略初始化完成");
    }
}