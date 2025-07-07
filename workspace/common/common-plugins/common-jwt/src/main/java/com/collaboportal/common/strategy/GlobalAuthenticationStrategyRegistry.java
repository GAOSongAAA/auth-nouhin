package com.collaboportal.common.strategy;

// 註：此類已遷移到 common-component 中，為了向後兼容而保留
// 新的實現位於：com.collaboportal.common.strategy.GlobalAuthenticationStrategyRegistry
// 建議使用 common-component 中的新實現

import com.collaboportal.common.strategy.AuthenticationStrategyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * 全局認證策略註冊中心
 * 統一管理來自不同插件的認證策略
 * 
 * 特點：
 * 1. 線程安全：使用 ConcurrentHashMap
 * 2. 插件隔離：每個插件可以獨立註冊策略
 * 3. 動態管理：支持運行時註冊和移除策略
 */
@Component("globalAuthenticationStrategyRegistry")
public class GlobalAuthenticationStrategyRegistry implements AuthenticationStrategyRegistry {

    private static final Logger logger = LoggerFactory.getLogger(GlobalAuthenticationStrategyRegistry.class);

    // 線程安全的策略存儲
    private final Map<String, LoginStrategy> strategies = new ConcurrentHashMap<>();

    // 插件註冊記錄（用於調試和監控）
    private final Map<String, String> strategyPluginMapping = new ConcurrentHashMap<>();

    @Override
    public void registerStrategy(String strategyKey, LoginStrategy strategy) {
        if (strategyKey == null || strategyKey.trim().isEmpty()) {
            throw new IllegalArgumentException("策略Key不能為空");
        }

        if (strategy == null) {
            throw new IllegalArgumentException("策略實現不能為空");
        }

        LoginStrategy existingStrategy = strategies.put(strategyKey, strategy);

        if (existingStrategy != null) {
            logger.warn("[策略註冊] 覆蓋已存在的策略: {}", strategyKey);
        } else {
            logger.info("[策略註冊] 成功註冊新策略: {}", strategyKey);
        }

        // 記錄調用來源（用於調試）
        String callerInfo = Thread.currentThread().getStackTrace()[2].getClassName();
        strategyPluginMapping.put(strategyKey, callerInfo);
    }

    @Override
    public LoginStrategy getStrategy(String strategyKey) {
        if (strategyKey == null || strategyKey.trim().isEmpty()) {
            logger.warn("[策略獲取] 策略Key為空");
            return null;
        }

        LoginStrategy strategy = strategies.get(strategyKey);

        if (strategy == null) {
            logger.warn("[策略獲取] 未找到策略: {}", strategyKey);
        } else {
            logger.debug("[策略獲取] 成功獲取策略: {}", strategyKey);
        }

        return strategy;
    }

    @Override
    public boolean hasStrategy(String strategyKey) {
        return strategyKey != null && strategies.containsKey(strategyKey);
    }

    @Override
    public boolean removeStrategy(String strategyKey) {
        if (strategyKey == null || strategyKey.trim().isEmpty()) {
            return false;
        }

        LoginStrategy removedStrategy = strategies.remove(strategyKey);
        strategyPluginMapping.remove(strategyKey);

        if (removedStrategy != null) {
            logger.info("[策略移除] 成功移除策略: {}", strategyKey);
            return true;
        } else {
            logger.warn("[策略移除] 策略不存在: {}", strategyKey);
            return false;
        }
    }

    @Override
    public String[] getRegisteredStrategyKeys() {
        return strategies.keySet().toArray(new String[0]);
    }

    /**
     * 獲取策略統計信息（用於監控和調試）
     */
    public Map<String, Object> getStrategyStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("totalStrategies", strategies.size());
        stats.put("registeredKeys", strategies.keySet());
        stats.put("pluginMapping", strategyPluginMapping);
        return stats;
    }

    /**
     * 清空所有策略（謹慎使用，主要用於測試）
     */
    public void clearAll() {
        logger.warn("[策略清理] 清空所有已註冊的策略");
        strategies.clear();
        strategyPluginMapping.clear();
    }
}