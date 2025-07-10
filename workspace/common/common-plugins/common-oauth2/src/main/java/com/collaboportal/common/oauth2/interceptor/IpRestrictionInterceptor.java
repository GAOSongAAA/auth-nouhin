package com.collaboportal.common.oauth2.interceptor;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.collaboportal.common.oauth2.annotation.IpRestricted;
import com.collaboportal.common.oauth2.utils.IpValidationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * IP地址限制攔截器
 * 在Controller方法執行前檢查@IpRestricted注解並驗證IP地址
 */
@Component
public class IpRestrictionInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(IpRestrictionInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        // 只處理HandlerMethod類型的請求
        if (!(handler instanceof HandlerMethod)) {
            logger.debug("請求不是HandlerMethod類型，跳過IP驗證");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        Class<?> beanType = handlerMethod.getBeanType();

        // 檢查方法級別的@IpRestricted注解（優先級較高）
        IpRestricted methodAnnotation = method.getAnnotation(IpRestricted.class);
        if (methodAnnotation != null) {
            return validateIpRestriction(request, response, methodAnnotation, "方法",
                    method.getDeclaringClass().getSimpleName() + "." + method.getName());
        }

        // 檢查類級別的@IpRestricted注解
        IpRestricted classAnnotation = beanType.getAnnotation(IpRestricted.class);
        if (classAnnotation != null) {
            return validateIpRestriction(request, response, classAnnotation, "類",
                    beanType.getSimpleName());
        }

        // 沒有IP限制注解，允許通過
        logger.debug("目標方法 {}#{} 沒有@IpRestricted注解，跳過IP驗證",
                beanType.getSimpleName(), method.getName());
        return true;
    }

    /**
     * 驗證IP限制
     * 
     * @param request    HTTP請求
     * @param response   HTTP響應
     * @param annotation IP限制注解
     * @param level      注解級別（方法或類）
     * @param targetInfo 目標信息（用於日誌）
     * @return 是否通過驗證
     */
    private boolean validateIpRestriction(HttpServletRequest request, HttpServletResponse response,
            IpRestricted annotation, String level, String targetInfo) throws Exception {

        // 檢查是否啟用IP限制
        if (!annotation.enabled()) {
            logger.debug("{}級別的IP限制已停用，目標: {} ，跳過驗證", level, targetInfo);
            return true;
        }

        // 獲取客戶端IP地址
        String clientIp = IpValidationUtil.getClientIpAddress(request);
        logger.info("開始進行{}級別的IP驗證，目標: {}，客戶端IP: {}", level, targetInfo, clientIp);

        // 驗證IP是否在允許列表中
        boolean isAllowed = IpValidationUtil.isIpAllowed(clientIp, annotation.allowedIps());

        if (isAllowed) {
            logger.info("{}級別IP驗證通過，目標: {}，客戶端IP [{}] 在允許範圍內", level, targetInfo, clientIp);
            return true;
        } else {
            logger.warn("{}級別IP驗證失敗，目標: {}，客戶端IP [{}] 不在允許範圍內",
                    level, targetInfo, clientIp);

            // 設置HTTP狀態碼為403 Forbidden
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            // 返回JSON格式的錯誤訊息
            String errorMessage = String.format(
                    "{\"error\":\"IP_ACCESS_DENIED\",\"message\":\"%s\",\"clientIp\":\"%s\",\"timestamp\":\"%s\"}",
                    annotation.message(),
                    clientIp,
                    java.time.Instant.now().toString());

            response.getWriter().write(errorMessage);
            response.getWriter().flush();

            return false;
        }
    }
}