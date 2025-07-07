package com.collaboportal.common.spring;

import com.collaboportal.common.controller.CustomErrorController;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * エラー関連の自動設定クラス
 * Spring Bootの自動設定機能を提供する
 */
@AutoConfiguration
public class CommonErrorAutoConfiguration {

    /**
     * カスタムエラーコントローラーのBeanを生成する
     * 既にCustomErrorControllerのBeanが存在しない場合のみ生成する
     * @return CustomErrorController カスタムエラーコントローラー
     */
    @Bean
    @ConditionalOnMissingBean(CustomErrorController.class)
    public CustomErrorController customErrorController() {
        return new CustomErrorController();
    }
}
