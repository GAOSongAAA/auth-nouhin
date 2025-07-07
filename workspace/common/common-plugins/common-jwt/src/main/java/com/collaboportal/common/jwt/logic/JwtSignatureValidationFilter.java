package com.collaboportal.common.jwt.logic;

import com.collaboportal.common.funcs.hooks.AuthFilterErrorStrategyFunction;
import com.collaboportal.common.funcs.hooks.AuthFilterStrategyFunction;
import com.collaboportal.common.jwt.context.JwtValidationContext;
import com.collaboportal.common.jwt.logic.callbacklogin.JwtValidationChain;
import com.collaboportal.common.jwt.logic.callbacklogin.JwtValidationPreHandler;

import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.jwt.utils.JwtValidationUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT署名検証フィルター - 責任チェーン + 関数型フック構成
 * リクエストごとにJWTの署名検証を行うフィルター
 */
public class JwtSignatureValidationFilter extends OncePerRequestFilter {

    // ロガー
    private static final Logger logger = LoggerFactory.getLogger(JwtSignatureValidationFilter.class);

    // JWT検証チェーン
    private final JwtValidationChain jwtValidationChain;
    // 認証前処理関数
    private final AuthFilterStrategyFunction beforeAuth;
    // 認証処理関数
    private final AuthFilterStrategyFunction auth;
    // エラー処理関数
    private final AuthFilterErrorStrategyFunction error;

    /**
     * コンストラクタ
     * 
     * @param jwtTokenUtil JWTトークン処理ユーティリティ
     */
    public JwtSignatureValidationFilter(JwtTokenUtil jwtTokenUtil) {
        // 検証チェーンの初期化
        JwtValidationPreHandler preHandler = new JwtValidationPreHandler(jwtTokenUtil);
        this.jwtValidationChain = preHandler.buildValidationChain();

        // 認証前処理の定義
        // rパラメータを検出した場合、Cookieに設定する
        this.beforeAuth = (request, response, chain) -> {
            String r = request.getParameter("r");
            if (r != null && !r.isBlank()) {
                logger.debug("[BeforeAuth] rパラメータを検出しました: {}", r);
                JwtValidationUtils.setCookie(response, "r", r);
            }
        };

        // 認証処理の定義
        // 検証チェーンを実行し、失敗した場合はリダイレクト、成功した場合は次のフィルターに処理を渡す
        this.auth = (request, response, chain) -> {
            JwtValidationContext context = JwtValidationContext.builder()
                    .request(request)
                    .response(response)
                    .build();
            boolean result = jwtValidationChain.execute(context);
            if (!result) {
                logger.warn("[Auth] 認証失敗 → リダイレクト: {}", context.getRedirectUrl());
                // response.sendRedirect(context.getRedirectUrl()); メソッドを呼び出す
                redirectToLogin(response, context.getRedirectUrl());
                return;
            }
            logger.info("[Auth] 認証成功");
            chain.doFilter(request, response);
        };

        // エラー処理の定義
        // 認証中に例外が発生した場合の処理
        this.error = e -> {
            logger.error("[Error] 認証中に例外が発生しました: {}", e.getMessage(), e);

        };
    }

    /**
     * フィルターの主要処理
     * 
     * @param request     HTTPリクエスト
     * @param response    HTTPレスポンス
     * @param filterChain フィルターチェーン
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 認証前処理を実行
            beforeAuth.handle(request, response, filterChain);

            // 認証処理を実行
            auth.handle(request, response, filterChain);
        } catch (Throwable e) {
            // エラー処理を実行
            error.handle(e);
            logger.error(e.getMessage());
            logger.error("認証失敗、内部エラーステータスを返します");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * リダイレクト先のURLを設定する
     * 
     * @param response  HTTPレスポンス
     * @param loginUrl  リダイレクト先のURL
     * @throws IOException 入出力例外
     */
    private void redirectToLogin(HttpServletResponse response, String loginUrl) throws IOException {
        logger.info("リダイレクト先のURL: {}", loginUrl);
        response.setHeader("Location", loginUrl);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302リダイレクト
    }
}
