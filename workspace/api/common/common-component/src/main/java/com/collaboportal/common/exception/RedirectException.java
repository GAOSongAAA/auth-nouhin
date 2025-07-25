package com.collaboportal.common.exception;

import lombok.Getter;

/**
 * 重定向异常
 * 一个特殊的、用于控制流程的异常，当认证失败且需要重定向到登录页或OAuth授权页时抛出。
 * Filter层将捕获此异常并执行重定向。
 */
@Getter
public class RedirectException extends RuntimeException {
    private final String redirectUrl;

    public RedirectException(String redirectUrl) {
        super("Redirecting to " + redirectUrl);
        this.redirectUrl = redirectUrl;
    }
}
