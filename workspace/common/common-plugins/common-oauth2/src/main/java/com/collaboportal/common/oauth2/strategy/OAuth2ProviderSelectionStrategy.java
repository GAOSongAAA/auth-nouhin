// ========== 策略接口 - OAuth2提供商选择策略 ==========
package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;

/**
 * OAuth2提供商选择策略接口
 * 职责：定义提供商选择的策略契约
 * 优势：支持多种选择策略（路径、域名、参数等），易于扩展
 */
@FunctionalInterface
public interface OAuth2ProviderSelectionStrategy {
    /**
     * 根据上下文选择OAuth2提供商
     * 
     * @param context 提供商选择上下文
     * @return 选择的提供商ID，如果无法确定则返回null
     */
    String selectProvider(OAuth2ProviderContext context);
}
