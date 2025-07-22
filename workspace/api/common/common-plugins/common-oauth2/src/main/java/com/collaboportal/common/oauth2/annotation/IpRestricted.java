package com.collaboportal.common.oauth2.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * IPアドレス制限アノテーション
 * IPアドレス制限が必要なControllerメソッドまたはクラスをマークするために使用
 * 単一IP、IP範囲、CIDR形式をサポート
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpRestricted {

    /**
     * 許可されたIPアドレスリスト
     * 以下の形式をサポート：
     * - 単一IP: "192.168.1.1"
     * - IP範囲: "192.168.1.1-192.168.1.100"
     * - CIDR形式: "192.168.1.0/24"
     * 
     * @return 許可されたIPアドレス配列
     */
    String[] allowedIps() default {};

    /**
     * IP検証失敗時のエラーメッセージ
     * 
     * @return エラーメッセージ
     */
    String message() default "アクセス拒否：IPアドレスが許可範囲内にありません";

    /**
     * IP制限を有効にするかどうか
     * デフォルトはtrue、IP検査を一時的に無効にするために使用可能
     * 
     * @return 有効かどうか
     */
    boolean enabled() default true;
}