// ========== OAuth2提供商选择过滤器 ==========
package com.collaboportal.common.oauth2.filter;

import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.strategy.OAuth2ProviderSelectionStrategyComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OAuth2提供商选择过滤器
 * 在请求处理早期识别和缓存OAuth2提供商信息
 */
public class OAuth2ProviderSelectionFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ProviderSelectionFilter.class);
    private static final String PROVIDER_CONTEXT_ATTRIBUTE = "oauth2.provider.context";

    private final OAuth2ProviderSelectionStrategyComposer strategyComposer;

    public OAuth2ProviderSelectionFilter(OAuth2ProviderSelectionStrategyComposer strategyComposer) {
        this.strategyComposer = strategyComposer;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logger.debug("[OAuth2过滤器] 处理请求: {}", httpRequest.getRequestURI());

        try {
            // 构建提供商选择上下文
            OAuth2ProviderContext context = new OAuth2ProviderContext.Builder()
                    .request(httpRequest)
                    .response(httpResponse)
                    .build();

            // 尝试选择提供商（不强制，让后续组件决定是否需要）
            String providerId = strategyComposer.selectProvider(context);
            if (providerId != null) {
                context.setSelectedProviderId(providerId);
                logger.debug("[OAuth2过滤器] 预选择提供商: {}", providerId);
            }

            // 将上下文存储在请求属性中，供后续使用
            httpRequest.setAttribute(PROVIDER_CONTEXT_ATTRIBUTE, context);

        } catch (Exception e) {
            logger.warn("[OAuth2过滤器] 提供商选择失败，继续处理", e);
        }

        // 继续过滤器链
        chain.doFilter(request, response);
    }

    /**
     * 从请求中获取OAuth2提供商上下文
     */
    public static OAuth2ProviderContext getProviderContext(HttpServletRequest request) {
        return (OAuth2ProviderContext) request.getAttribute(PROVIDER_CONTEXT_ATTRIBUTE);
    }
}