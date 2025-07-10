package com.collaboportal.common.oauth2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IP地址限制注解
 * 用於標記需要IP地址限制的Controller方法或類
 * 支持單個IP、IP範圍和CIDR格式
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpRestricted {

    /**
     * 允許的IP地址列表
     * 支持以下格式：
     * - 單個IP: "192.168.1.1"
     * - IP範圍: "192.168.1.1-192.168.1.100"
     * - CIDR格式: "192.168.1.0/24"
     * 
     * @return 允許的IP地址陣列
     */
    String[] allowedIps() default {};

    /**
     * 當IP驗證失敗時的錯誤訊息
     * 
     * @return 錯誤訊息
     */
    String message() default "存取被拒絕：IP地址不在允許範圍內";

    /**
     * 是否啟用IP限制
     * 預設為true，可以用於臨時停用IP檢查
     * 
     * @return 是否啟用
     */
    boolean enabled() default true;
}