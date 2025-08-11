package com.collaboportal.common.filter;

import java.util.List;

import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;


@SuppressWarnings("rawtypes")
public interface CommonAuthorizationFilter {

    CommonAuthorizationFilter addInclude(String... patterns);

    CommonAuthorizationFilter addExclude(String... patterns);


    CommonAuthorizationFilter setIncludeList(List<String> pathList);

    /**
     * 写入 [ 放行路由 ] 集合
     * @param pathList 路由集合
     * @return 对象自身
     */
    CommonAuthorizationFilter setExcludeList(List<String> pathList); 

        // ------------------------ 钩子函数

    /**
     * 写入[ 认证函数 ]: 每次请求执行
     * @param auth see note
     * @return 对象自身
     */
    CommonAuthorizationFilter setAuth(AuthorizationStrategy auth);

    /**
     * 写入[ 前置函数 ]：在每次[ 认证函数 ]之前执行。
     *      <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
     * @param beforeAuth /
     * @return 对象自身
     */
    CommonAuthorizationFilter setBeforeAuth(AuthorizationStrategy beforeAuth);

    /**
     * 写入[ 错误处理函数 ]：在每次[ 认证函数 ]之后执行。
     * @param error /
     * @return 对象自身
     */
    CommonAuthorizationFilter setError(AuthorizationErrorStrategy error);
}
