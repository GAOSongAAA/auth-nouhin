package com.collaboportal.common.interceptor;

import com.collaboportal.common.annotation.RequiresRole;
import com.collaboportal.common.context.CommonHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    // @Override
    // public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    //     if (!(handler instanceof HandlerMethod)) {
    //         return true;
    //     }

    //     HandlerMethod handlerMethod = (HandlerMethod) handler;
    //     RequiresRole requiresRole = handlerMethod.getMethodAnnotation(RequiresRole.class);

    //     if (requiresRole == null) {
    //         return true;
    //     }

    //     Object userInfoObj = CommonHolder.getStorage().get("USER_INFO");
    //     if (userInfoObj == null || !(userInfoObj instanceof Map)) {
    //         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing user information.");
    //         return false;
    //     }

    //     Map<String, Object> userInfo = (Map<String, Object>) userInfoObj;
    //     Object rolesObj = userInfo.get("roles");

    //     if (rolesObj == null || !(rolesObj instanceof List)) {
    //         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User roles are not available.");
    //         return false;
    //     }

    //     List<String> userRoles = (List<String>) rolesObj;
    //     List<String> requiredRoles = Arrays.asList(requiresRole.value());
    //     boolean hasPermission = userRoles.stream().anyMatch(requiredRoles::contains);

    //     if (hasPermission) {
    //         return true;
    //     } else {
    //         response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have the required role to access this resource.");
    //         return false;
    //     }
    // }
}
