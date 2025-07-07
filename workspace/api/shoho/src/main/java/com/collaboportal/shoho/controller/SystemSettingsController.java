package com.collaboportal.shoho.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.shoho.model.SystemSettingsResponseBody;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/system_setting")
public class SystemSettingsController {
    
    // パスワード変更URL
    @Value("${URL_PASS_CHANGE}")
    private String passChangeUrl;

    // ログアウトURL
    @Value("${URL_LOGOUT}")
    private String logoutUrl;

    // clientID
    @Value("${collaboportal_client_id_web}")
    private String clientId;

    // baseURL
    @Value("${collaboportal_baseurl}")
    private String baseUrl;

    /**
     * システム設定値取得
     * @return SystemSettingsResponseBody システム設定値取得レスポンスボディ
     */
    @GetMapping()
    public SystemSettingsResponseBody getSystemSettings() {

        // ログアウトURL作成
        String createLogoutUrl = logoutUrl + "?returnTo=" + baseUrl + "&client_id=" + clientId;
        return new SystemSettingsResponseBody(passChangeUrl, createLogoutUrl);
    }
}
