package com.collaboportal.common.strategy.common;



/**
 * 认证策略函数式接口 (对应 setAuth 和 setBeforeAuth)
 */
@FunctionalInterface
public interface CommonStrategy {
    void run(Object object) throws Throwable;
}