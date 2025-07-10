package com.collaboportal.common.oauth2.handler;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import com.collaboportal.common.oauth2.annotation.IpRestricted;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.utils.IpValidationUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP地址驗證處理器
 * 檢查當前請求的目標方法是否有@IpRestricted注解
 * 如果有，則驗證客戶端IP是否在允許範圍內
 */
public class IpValidationHandler implements JwtValidationHandler {

    private static final Logger logger = LoggerFactory.getLogger(IpValidationHandler.class);

    @Override
    public boolean handle(OAuth2ProviderContext context) {
        HttpServletRequest request = context.getRequest();

        try {
            // 獲取目標處理方法
            Object handler = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (!(handler instanceof HandlerMethod)) {
                // 如果不是HandlerMethod，可能是靜態資源請求，允許通過
                logger.debug("請求不是HandlerMethod類型，跳過IP驗證");
                return true;
            }

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            Class<?> beanType = handlerMethod.getBeanType();

            // 檢查方法級別的@IpRestricted注解
            IpRestricted methodAnnotation = method.getAnnotation(IpRestricted.class);
            if (methodAnnotation != null) {
                return validateIpRestriction(request, methodAnnotation, "方法");
            }

            // 檢查類級別的@IpRestricted注解
            IpRestricted classAnnotation = beanType.getAnnotation(IpRestricted.class);
            if (classAnnotation != null) {
                return validateIpRestriction(request, classAnnotation, "類");
            }

            // 沒有IP限制注解，允許通過
            logger.debug("目標方法沒有@IpRestricted注解，跳過IP驗證");
            return true;

        } catch (Exception e) {
            logger.error("IP驗證過程中發生異常: {}", e.getMessage(), e);
            // 發生異常時為了安全起見拒絕請求
            return false;
        }
    }

    /**
     * 驗證IP限制
     * 
     * @param request    HTTP請求
     * @param annotation IP限制注解
     * @param level      注解級別（方法或類）
     * @return 是否通過驗證
     */
    private boolean validateIpRestriction(HttpServletRequest request, IpRestricted annotation, String level) {
        // 檢查是否啟用IP限制
        if (!annotation.enabled()) {
            logger.debug("{}級別的IP限制已停用，跳過驗證", level);
            return true;
        }

        // 獲取客戶端IP地址
        String clientIp = IpValidationUtil.getClientIpAddress(request);
        logger.info("開始進行{}級別的IP驗證，客戶端IP: {}", level, clientIp);

        // 驗證IP是否在允許列表中
        boolean isAllowed = IpValidationUtil.isIpAllowed(clientIp, annotation.allowedIps());

        if (isAllowed) {
            logger.info("{}級別IP驗證通過，客戶端IP [{}] 在允許範圍內", level, clientIp);
            return true;
        } else {
            logger.warn("{}級別IP驗證失敗，客戶端IP [{}] 不在允許範圍內。錯誤訊息: {}",
                    level, clientIp, annotation.message());

            // 在context中設置錯誤信息，可以被後續的錯誤處理使用
            request.setAttribute("ip.validation.error", annotation.message());
            request.setAttribute("ip.validation.client.ip", clientIp);

            return false;
        }
    }
}