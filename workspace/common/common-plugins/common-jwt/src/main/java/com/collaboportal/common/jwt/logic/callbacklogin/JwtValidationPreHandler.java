package com.collaboportal.common.jwt.logic.callbacklogin;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.context.JwtValidationContext;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.jwt.utils.JwtValidationUtils;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT認証プリプロセッサ、責任チェーン構造を構築
 */
public class JwtValidationPreHandler {

    private static final Logger logger = LoggerFactory.getLogger(JwtValidationPreHandler.class);

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtTokenStrategyRegistry strategyRegistry;

    private final String localAuthPage = "testEnv";

    public JwtValidationPreHandler(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.strategyRegistry = new JwtTokenStrategyRegistry();
        registerDefaultStrategies();
        logger.debug("JwtValidationPreHandlerの初期化が完了しました。JwtTokenUtilを注入しました");
    }

    private void registerDefaultStrategies() {
        strategyRegistry.register("header", JwtValidationUtils::extractTokenFromHeader);
        strategyRegistry.register("cookie", JwtValidationUtils::extractTokenFromCookie);
        logger.debug("デフォルトのトークン抽出ストラテジーを登録しました: header, cookie");
    }

    public JwtValidationChain buildValidationChain() {
        JwtValidationChain chain = new JwtValidationChain();
        chain.addHandler(this::rParamHandler);
        chain.addHandler(this::cookieCheckHandler);
        chain.addHandler(this::tokenValidationHandler);
        return chain;
    }

    private boolean rParamHandler(JwtValidationContext context) {
        String rParam = context.getRequest().getParameter("r");
        if (rParam != null && !rParam.isBlank()) {
            JwtValidationUtils.setCookie(context.getResponse(), "r", rParam);
            logger.debug("rパラメータを処理し、Cookieに書き込みました: {}", rParam);
        }
        return true;
    }

    private boolean cookieCheckHandler(JwtValidationContext context) {
        HttpServletRequest req = context.getRequest();
        Cookie[] cookies = req.getCookies();

        if (cookies == null || cookies.length == 0) {
            logger.debug("Cookieが検出されませんでした");

            if (!JwtValidationUtils.isUseCookieAuthorization(req)) {
                logger.warn("現在のパスではCookieモードが許可されていません。認証を拒否します");
                context.setRedirectUrl(getRedirectUrlByEnv());
                return false;
            }

            context.setRedirectUrl(getRedirectUrlByEnv());
            return false;
        }

        logger.debug("Cookieが検出されました");
        return true;
    }

    private boolean tokenValidationHandler(JwtValidationContext context) {
        HttpServletRequest req = context.getRequest();
        HttpServletResponse resp = context.getResponse();

        String strategyKey = JwtValidationUtils.decideStrategyByPath(req);
        context.setStrategyKey(strategyKey);
        String token = strategyRegistry.resolveToken(req, strategyKey);
        context.setToken(token);

        logger.debug("ストラテジー [{}] を使用してトークンを取得しました: {}", strategyKey, token != null ? "[存在]" : "null");

        if (token == null || token.isBlank()) {
            logger.debug("トークンが存在しません。認証に失敗しました");
            context.setRedirectUrl(getRedirectUrlByEnv());
            return false;
        }

        try {
            if (jwtTokenUtil.isTokenExpired(token)) {
                logger.debug("トークンの有効期限が切れています");
                context.setRedirectUrl(getRedirectUrlByEnv());
                return false;
            }

            String updatedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            String userType = jwtTokenUtil.getHonbuFlg(updatedToken);
            logger.debug("トークンの検証に成功しました。新しいトークン: {}, ユーザータイプ: {}", updatedToken, userType);

            JwtValidationUtils.setCookie(resp, Message.Cookie.AUTH, updatedToken);
            JwtValidationUtils.setCookie(resp, Message.Cookie.HONBU_FLAG, userType);
            return true;

        } catch (Exception e) {
            logger.error("トークン検証中に例外が発生しました: {}", e.getMessage(), e);
            context.setRedirectUrl(getRedirectUrlByEnv());
            return false;
        }
    }

    private String getRedirectUrlByEnv() {
        return "0".equals(ConfigManager.getConfig().getEnvFlag())
                ? localAuthPage
                : JwtValidationUtils.buildAuthRedirectUrl();
    }
}
