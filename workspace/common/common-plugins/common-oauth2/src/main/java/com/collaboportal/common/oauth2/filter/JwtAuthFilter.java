package com.collaboportal.common.oauth2.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.collaboportal.common.funcs.hooks.AuthFilterErrorStrategyFunction;
import com.collaboportal.common.funcs.hooks.AuthFilterStrategyFunction;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.template.ext.JwtValidationTemplate;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;

public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtValidationChain jwtValidationChain;
    // 認証前処理関数
    private final AuthFilterStrategyFunction beforeAuth;
    // 認証処理関数
    private final AuthFilterStrategyFunction auth;
    // エラー処理関数
    private final AuthFilterErrorStrategyFunction error;

    public JwtAuthFilter(JwtTokenUtil jwtTokenUtil, OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        JwtValidationTemplate jwtValidationTemplate = new JwtValidationTemplate(jwtTokenUtil,
                clientRegistrationFactory);
        this.jwtValidationChain = jwtValidationTemplate.buildValidationChain();

        this.beforeAuth = (request, response, chain) -> {
            String r = request.getParameter("r");
            if (r != null && !r.isBlank()) {
                logger.debug("[BeforeAuth] rパラメータを検出しました: {}", r);
                JwtValidationUtils.setCookie(response, "r", r);
            }
        };

        this.auth = (request, response, chain) -> {
            OAuth2ProviderContext context = OAuth2ProviderContext.builder()
                    .request(request)
                    .response(response)
                    .build();
            boolean result = jwtValidationChain.execute(context);
            if (!result) {
                logger.warn("[Auth] 認証失敗 → リダイレクト: {}", context.getAuthProviderUrl());
                redirectToLogin(response, context.getAuthProviderUrl());
                return;
            }
            logger.info("[Auth] 認証成功");
            chain.doFilter(request, response);
        };

        this.error = e -> {
            logger.error("[Error] 認証中に例外が発生しました: {}", e.getMessage(), e);
        };
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            beforeAuth.handle(request, response, filterChain);
            auth.handle(request, response, filterChain);
        } catch (Throwable e) {
            error.handle(e);
            logger.error(e.getMessage());
            logger.error("認証失敗、内部エラーステータスを返します");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * リダイレクト先のURLを設定する
     * 
     * @param response HTTPレスポンス
     * @param loginUrl リダイレクト先のURL
     * @throws IOException 入出力例外
     */
    private void redirectToLogin(HttpServletResponse response, String loginUrl) throws IOException {
        logger.info("リダイレクト先のURL: {}", loginUrl);
        response.setHeader("Location", loginUrl);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302リダイレクト
    }

}
