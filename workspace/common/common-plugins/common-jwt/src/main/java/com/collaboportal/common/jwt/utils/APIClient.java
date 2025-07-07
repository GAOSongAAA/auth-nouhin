package com.collaboportal.common.jwt.utils;

import java.util.concurrent.TimeUnit;

import com.collaboportal.common.jwt.service.APIClientService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {

    /** サービス */
    APIClientService apiClientService;

    /** 
     * セットアップ
     * 
     * @param baseUrl ベースURL
     */
    public APIClient(String baseUrl) {
        

            OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // 接続タイムアウト
            .readTimeout(30, TimeUnit.SECONDS) // 読み込みタイムアウト
            .writeTimeout(30, TimeUnit.SECONDS) // 書き込みタイムアウト
            .build();
            
            Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();
            
            apiClientService = retrofit.create(APIClientService.class);
    }

    /** 
     * サービス取得
     * 
     * @return サービス
     */
    public APIClientService getClient() {
        return apiClientService;
    }
}
