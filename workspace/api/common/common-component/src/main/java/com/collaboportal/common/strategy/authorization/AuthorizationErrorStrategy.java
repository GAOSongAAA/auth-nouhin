package com.collaboportal.common.strategy.authorization;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

/**
 * 错误处理策略函数式接口 (对应 setError)
 */
@FunctionalInterface
public interface AuthorizationErrorStrategy {
    void run(Throwable e, BaseRequest request, BaseResponse response);
}