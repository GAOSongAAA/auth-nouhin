package com.collaboportal.common.oauth2.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.collaboportal.common.filter.AuthFilter;
import com.collaboportal.common.funcs.hooks.AuthFilterErrorStrategyFunction;
import com.collaboportal.common.funcs.hooks.AuthFilterStrategyFunction;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.template.ext.JwtValidationTemplate;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;
import com.collaboportal.common.strategy.AuthStrategy;

public class JwtAuthFilter extends OncePerRequestFilter implements AuthFilter<OAuth2ProviderContext> {

    /**
	 * インターセプトルート
	 */
	public List<String> includeList = new ArrayList<>();

	/**
	 * パススルールート
	 */
	public List<String> excludeList = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtValidationChain jwtValidationChain;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    // 認証前処理関数
    private AuthFilterStrategyFunction beforeAuth;
    // 認証処理関数
    private AuthFilterStrategyFunction auth;
    // エラー処理関数
    private AuthFilterErrorStrategyFunction error;

    public JwtAuthFilter(JwtTokenUtil jwtTokenUtil, OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        JwtValidationTemplate jwtValidationTemplate = new JwtValidationTemplate(jwtTokenUtil,
                clientRegistrationFactory);
        this.jwtValidationChain = jwtValidationTemplate.buildValidationChain();

        this.beforeAuth = (request, response, chain) -> {
            String r = request.getParameter("r");
            if (r != null && !r.isBlank()) {
                logger.debug("[BeforeAuth] rパラメータを検出: {}", r);
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
            logger.error("[Error] 認証プロセス中に例外が発生: {}", e.getMessage(), e);
        };
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        logger.debug("リクエストパスを処理: {}", requestPath);

        try {
            // 前処理関数は常に実行、ルート制限を受けない
            beforeAuth.handle(request, response, filterChain);

            // 認証が必要かどうかをチェック
            if (shouldAuthenticate(requestPath)) {
                logger.debug("パス {} は認証が必要", requestPath);
                auth.handle(request, response, filterChain);
            } else {
                logger.debug("パス {} は認証をスキップ、直接パススルー", requestPath);
                filterChain.doFilter(request, response);
            }
        } catch (Throwable e) {
            error.handle(e);
            logger.error(e.getMessage());
            logger.error("認証失敗、内部エラー状態を返す");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 認証が必要かどうかを判断
     * 
     * @param requestPath リクエストパス
     * @return true 認証が必要、false 認証不要
     */
    private boolean shouldAuthenticate(String requestPath) {
        // 除外リストにある場合は認証不要
        for (String excludePattern : excludeList) {
            if (pathMatcher.match(excludePattern, requestPath)) {
                logger.debug("パス {} が除外パターンにマッチ {}", requestPath, excludePattern);
                return false;   
            }
        }

        // 包含リストが空の場合は、すべてのパスで認証が必要（除外されたもの以外）
        if (includeList.isEmpty()) {
            return true;
        }

        // 包含リストにある場合は認証が必要
        for (String includePattern : includeList) {
            if (pathMatcher.match(includePattern, requestPath)) {
                logger.debug("パス {} が包含パターンにマッチ {}", requestPath, includePattern);
                return true;
            }
        }

        // 包含リストにない場合は認証不要
        return false;
    }

    /**
     * リダイレクト先URLを設定
     * 
     * @param response HTTPレスポンス
     * @param loginUrl リダイレクト先URL
     * @throws IOException 入出力例外
     */
    private void redirectToLogin(HttpServletResponse response, String loginUrl) throws IOException {
        logger.info("リダイレクト先URL: {}", loginUrl);
        response.setHeader("Location", loginUrl);
        response.setStatus(HttpServletResponse.SC_FOUND); // 302リダイレクト
    }

    @Override
    public AuthFilter<OAuth2ProviderContext> addInclude(String... patterns) {
        includeList.addAll(Arrays.asList(patterns));
        return this;
    }

    @Override
    public AuthFilter<OAuth2ProviderContext> addExclude(String... patterns) {
        excludeList.addAll(Arrays.asList(patterns));
        return this;
    }

    @Override
    public AuthFilter<OAuth2ProviderContext> setIncludeList(List<String> pathList) {
        includeList = pathList;
        return this;
    }

    @Override
    public AuthFilter<OAuth2ProviderContext> setExcludeList(List<String> pathList) {
        excludeList = pathList;
        return this;
    }

    @Override
    public AuthFilter<OAuth2ProviderContext> setAuth(AuthStrategy<OAuth2ProviderContext> auth) {
        this.auth = (request, response, chain) -> {
            OAuth2ProviderContext context = OAuth2ProviderContext.builder()
                    .request(request)
                    .response(response)
                    .build();
            auth.login(context);
        };
        return this;
    }


    @Override
    public AuthFilter<OAuth2ProviderContext> setBeforeAuth(AuthStrategy<OAuth2ProviderContext> beforeAuth) {
        this.beforeAuth = (request, response, chain) -> {
            OAuth2ProviderContext context = OAuth2ProviderContext.builder()
                    .request(request)
                    .response(response)
                    .build();
            beforeAuth.login(context);
        };
        return this;
    }

}
