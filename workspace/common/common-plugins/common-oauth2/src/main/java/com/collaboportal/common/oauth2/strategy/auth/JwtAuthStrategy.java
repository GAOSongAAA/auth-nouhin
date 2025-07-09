package com.collaboportal.common.oauth2.strategy.auth;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface JwtAuthStrategy {
    String execute(HttpServletRequest request);
}
