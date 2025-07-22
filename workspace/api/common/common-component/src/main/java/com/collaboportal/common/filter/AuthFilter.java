package com.collaboportal.common.filter;

import java.util.List;

import com.collaboportal.common.strategy.AuthStrategy;

@SuppressWarnings("rawtypes")
public interface AuthFilter<T> {

    AuthFilter addInclude(String... patterns);

    AuthFilter addExclude(String... patterns);


    AuthFilter setIncludeList(List<String> pathList);

    /**
     * 写入 [ 放行路由 ] 集合
     * @param pathList 路由集合
     * @return 对象自身
     */
    AuthFilter setExcludeList(List<String> pathList); 

        // ------------------------ 钩子函数

    /**
     * 写入[ 认证函数 ]: 每次请求执行
     * @param auth see note
     * @return 对象自身
     */
    AuthFilter setAuth(AuthStrategy<T> auth);

    /**
     * 写入[ 前置函数 ]：在每次[ 认证函数 ]之前执行。
     *      <b>注意点：前置认证函数将不受 includeList 与 excludeList 的限制，所有路由的请求都会进入 beforeAuth</b>
     * @param beforeAuth /
     * @return 对象自身
     */
    AuthFilter setBeforeAuth(AuthStrategy<T> beforeAuth);
}
