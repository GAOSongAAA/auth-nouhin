package com.collaboportal.common.strategy;

/**
 * 認證策略註冊中心抽象接口
 * 提供統一的策略管理契約，支持插件化擴展
 * 
 * 設計模式：策略模式 + 註冊表模式
 * 解耦目標：各插件可以獨立註冊自己的認證策略
 */
public interface AuthenticationStrategyRegistry {

    /**
     * 註冊認證策略
     * 
     * @param strategyKey 策略標識符
     * @param strategy    認證策略實現
     */
    void registerStrategy(String strategyKey, LoginStrategy strategy);

    /**
     * 獲取認證策略
     * 
     * @param strategyKey 策略標識符
     * @return 對應的認證策略，如果不存在則返回 null
     */
    LoginStrategy getStrategy(String strategyKey);

    /**
     * 檢查策略是否存在
     * 
     * @param strategyKey 策略標識符
     * @return 策略是否已註冊
     */
    boolean hasStrategy(String strategyKey);

    /**
     * 移除認證策略
     * 
     * @param strategyKey 策略標識符
     * @return 是否成功移除
     */
    boolean removeStrategy(String strategyKey);

    /**
     * 獲取所有已註冊的策略Key
     * 
     * @return 策略Key集合
     */
    String[] getRegisteredStrategyKeys();
}