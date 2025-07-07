package com.collaboportal.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

import com.collaboportal.common.config.BaseConfig;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.config.CommonConfigFactory;
import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.filter.LogTraceIdFilter;

/**
 * 設定管理クラス
 * アプリケーション全体の設定とフィルターを管理する
 */
public class ConfigManager {

    // 設定クラスとそのインスタンスを保持するマップ
    private static final Map<Class<? extends BaseConfig>, BaseConfig> configMap = new ConcurrentHashMap<>();

    /**
     * 設定を登録する
     * 
     * @param config 登録する設定オブジェクト
     */
    public static void setConfig(BaseConfig config) {
        if (config != null) {
            configMap.put(config.getClass(), config);
        }
    }

    /**
     * 指定された設定クラスのインスタンスを取得する
     * 
     * @param <T>         設定クラスの型
     * @param configClass 取得する設定クラス
     * @return 設定オブジェクト
     */
    public static <T extends BaseConfig> T getConfig(Class<T> configClass) {
        T config = (T) configMap.get(configClass);
        if (config == null) {
            synchronized (ConfigManager.class) {
                config = (T) configMap.get(configClass);
                if (config == null) {
                    config = CommonConfigFactory.createConfig(configClass);
                    configMap.put(configClass, config);
                }
            }
        }
        return config;
    }

    /**
     * CommonConfigのインスタンスを取得する便利メソッド
     * 
     * @return CommonConfigオブジェクト
     */
    public static CommonConfig getConfig() {
        return getConfig(CommonConfig.class);
    }

    /**
     * LogMaskConfigのインスタンスを取得する便利メソッド
     * 
     * @return LogMaskConfigオブジェクト
     */
    public static LogMaskConfig getLogMaskConfig() {
        return getConfig(LogMaskConfig.class);
    }

    // ログトレースIDフィルターの登録Bean
    private volatile static FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilterBean;
    // ログブックフィルターの登録Bean
    private volatile static FilterRegistrationBean<LogbookFilter> logbookFilterBean;

    /**
     * ログトレースIDフィルターを取得する
     * 
     * @return LogTraceIdFilterインスタンス
     */
    public static LogTraceIdFilter getLogTraceIdFilter() {
        if (logTraceIdFilterBean == null) {
            synchronized (ConfigManager.class) {
                if (logTraceIdFilterBean == null) {
                    FilterRegistrationBean<LogTraceIdFilter> bean = new FilterRegistrationBean<>();
                    bean.setFilter(new LogTraceIdFilter());
                    bean.addUrlPatterns("/*");
                    bean.setOrder(1);
                    setLogTraceIdFilter(bean);
                }
            }
        }
        return logTraceIdFilterBean.getFilter();
    }

    /**
     * ログトレースIDフィルターを設定する
     * 
     * @param filter 設定するフィルター
     */
    public static void setLogTraceIdFilter(FilterRegistrationBean<LogTraceIdFilter> filter) {
        logTraceIdFilterBean = filter;
    }

    /**
     * ログブックフィルターを取得する
     * 
     * @param logbook Logbookインスタンス
     * @return LogbookFilterインスタンス
     */
    public static LogbookFilter getLogbookFilter(Logbook logbook) {
        if (logbookFilterBean == null) {
            synchronized (ConfigManager.class) {
                if (logbookFilterBean == null) {
                    FilterRegistrationBean<LogbookFilter> bean = new FilterRegistrationBean<>();
                    bean.setFilter(new LogbookFilter(logbook));
                    bean.addUrlPatterns("/*");
                    bean.setOrder(1);
                    setLogbookFilter(bean);
                }
            }
        }
        return logbookFilterBean.getFilter();
    }

    /**
     * ログブックフィルターを設定する
     * 
     * @param filter 設定するフィルター
     */
    public static void setLogbookFilter(FilterRegistrationBean<LogbookFilter> filter) {
        logbookFilterBean = filter;
    }

}