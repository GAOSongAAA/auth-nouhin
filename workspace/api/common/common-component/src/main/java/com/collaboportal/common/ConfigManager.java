package com.collaboportal.common;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

import com.collaboportal.common.config.BaseConfig;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.config.CommonConfigFactory;
import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.context.CommonContext;
import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.common.filter.LogTraceIdFilter;
import com.collaboportal.common.login.LoginTemplate;
import com.collaboportal.common.utils.LoginUtil;

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

    private volatile static CommonContext commonContext;

    public static CommonContext getCommonContext() {
        if(commonContext != null && commonContext.isValid()) {
            return commonContext;
        }else{
            throw new RuntimeException("コンテキスト無効です。");
        }
    }

    public static Map<String, LoginTemplate> loginTemplateMap = new LinkedHashMap<>();

    public static void putLoginTemplate(LoginTemplate loginTemplate) {
		loginTemplateMap.put(loginTemplate.getLoginType(), loginTemplate);
	}
    public static void removeLoginTemplate(String loginType) {
		loginTemplateMap.remove(loginType);
	}

    public static LoginTemplate getLoginTemplate(String loginType) {
		return loginTemplateMap.get(loginType);
	}

    public static LoginTemplate getLoginTemplate(String loginType, boolean isCreate) {
		// 如果type为空则返回框架默认内置的 
		if(loginType == null || loginType.isEmpty()) {
			return LoginUtil.loginTemplate;
		}
		
		// 从集合中获取 
		LoginTemplate loginTemplate = loginTemplateMap.get(loginType);
		if(loginTemplate == null) {
			
			// isCreate=true时，自创建模式：自动创建并返回 
			if(isCreate) {
				synchronized (ConfigManager.class) {
					loginTemplate = loginTemplateMap.get(loginType);
					if(loginTemplate == null) {
						loginTemplate = new LoginTemplate(loginType);
					}
				}
			} 
			// isCreate=false时，严格校验模式：抛出异常 
			else {
				/*
				 * 此时有两种情况会造成 StpLogic == null 
				 * 1. loginType拼写错误，请改正 （建议使用常量） 
				 * 2. 自定义StpUtil尚未初始化（静态类中的属性至少一次调用后才会初始化），解决方法两种
				 * 		(1) 从main方法里调用一次
				 * 		(2) 在自定义StpUtil类加上类似 @Component 的注解让容器启动时扫描到自动初始化 
				 */
				throw new CommonException(InternalErrorCode.UNDEINED_ERROR);
			}
		}
		
		// 返回 
		return loginTemplate;
	}


}