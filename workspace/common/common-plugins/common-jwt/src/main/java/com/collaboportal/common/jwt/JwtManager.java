package com.collaboportal.common.jwt;

import org.springframework.boot.web.servlet.FilterRegistrationBean;

import com.collaboportal.common.filter.MaintenanceModeFilter;
import com.collaboportal.common.jwt.logic.JwtSignatureValidationFilter;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;

/**
 * JWT管理クラス
 * JWT関連のフィルター設定を管理する
 */
public class JwtManager {

    // JWT署名検証フィルターのBean
    private volatile static FilterRegistrationBean<JwtSignatureValidationFilter> jwtSignatureValidationFilterBean;
    
    /**
     * JWT署名検証フィルターを取得する
     * シングルトンパターンで実装
     * @param jwtTokenUtil JWTトークン関連ユーティリティ
     * @return JWT署名検証フィルター
     */
    public static JwtSignatureValidationFilter getJwtSignatureValidationFilter(JwtTokenUtil jwtTokenUtil) {
        if(jwtSignatureValidationFilterBean == null) {
            synchronized(JwtManager.class) {
                if(jwtSignatureValidationFilterBean == null) {
                    FilterRegistrationBean<JwtSignatureValidationFilter> bean = new FilterRegistrationBean<>();
                    bean.setFilter(new JwtSignatureValidationFilter(jwtTokenUtil));
                    // フィルターを適用するURLパターン
                    bean.addUrlPatterns("/ms/*", "/ms", "/honbu", "/honbu/*", "/");
                    bean.setName("jwtSignatureValidationFilter");
                    // フィルターの実行順序
                    bean.setOrder(4);
                    setJwtSignatureValidationFilter(bean);
                }
            }
        }
        return jwtSignatureValidationFilterBean.getFilter();
    }

    /**
     * JWT署名検証フィルターを設定する
     * @param filter フィルターのBean
     */
    public static void setJwtSignatureValidationFilter(FilterRegistrationBean<JwtSignatureValidationFilter> filter) {
        jwtSignatureValidationFilterBean = filter;
    }

    // メンテナンスモードフィルターのBean
    private volatile static FilterRegistrationBean<MaintenanceModeFilter> maintenanceModeFilterBean;

    /**
     * メンテナンスモードフィルターを取得する
     * シングルトンパターンで実装
     * @return メンテナンスモードフィルター
     */
    public static MaintenanceModeFilter getMaintenanceModeFilter() {
        if(maintenanceModeFilterBean == null) {
            synchronized(JwtManager.class) {
                if(maintenanceModeFilterBean == null) {
                    FilterRegistrationBean<MaintenanceModeFilter> bean = new FilterRegistrationBean<>();
                    bean.setFilter(new MaintenanceModeFilter());
                    // フィルターを適用するURLパターン
                    bean.addUrlPatterns("/ms/*", "/ms", "/honbu", "/honbu/*", "/", "/auth/callback", "/index.html");
                    // フィルターの実行順序
                    bean.setOrder(3);
                    setMaintenanceModeFilter(bean);
                }
            }
        }
        return maintenanceModeFilterBean.getFilter();
    }

    /**
     * メンテナンスモードフィルターを設定する
     * @param filter フィルターのBean
     */
    public static void setMaintenanceModeFilter(FilterRegistrationBean<MaintenanceModeFilter> filter) {
        maintenanceModeFilterBean = filter;
    }
}
