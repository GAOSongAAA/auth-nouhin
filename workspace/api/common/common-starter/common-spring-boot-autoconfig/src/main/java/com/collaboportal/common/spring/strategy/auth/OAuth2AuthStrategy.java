package com.collaboportal.common.spring.strategy.auth;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.RedirectException;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.jwt.utils.JwtValidationUtils;
import com.collaboportal.common.strategy.auth.AuthenticationStrategy;
import com.collaboportal.common.utils.Message;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("oauth2AuthStrategy")
public class OAuth2AuthStrategy implements AuthenticationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthStrategy.class);
    private final JwtTokenUtil jwtTokenUtil;

    public OAuth2AuthStrategy(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException, RedirectException {
        logger.debug("Executing OAuth2 Authentication Strategy...");

        String token = request.getCookieValue(Message.Cookie.AUTH);

        if (token != null && !token.isEmpty()) {
            try {
                if (!jwtTokenUtil.isTokenExpired(token)) {
                    logger.info("Valid Auth_Token found in cookie. Refreshing token and proceeding.");
                    Map<String, String> userInfo = JwtTokenUtil.getItemsJwtToken(token);
                     if (userInfo.isEmpty()) {
                        logger.warn("Token is valid but contains no user information. Proceeding to OAuth2 flow.");
                        throw new RedirectException(JwtValidationUtils.buildAuthRedirectUrl());
                    }
                    CommonHolder.getStorage().set("USER_INFO", userInfo);
                    logger.debug("User information parsed and stored in request context: {}", userInfo.get("sub"));

                    String refreshedToken = jwtTokenUtil.updateExpiresAuthToken(token);
                    response.addCookie(Message.Cookie.AUTH, refreshedToken, "/", null, -1);
                    logger.debug("Auth_Token has been refreshed.");

                    return;
                }
            } catch (ExpiredJwtException e) {
                logger.info("Auth_Token has expired. Proceeding to OAuth2 flow.");
            } catch (Exception e) {
                logger.warn("Invalid Auth_Token found. Proceeding to OAuth2 flow.", e);
            }
        }

        logger.info("Auth_Token is missing or invalid. Redirecting to OAuth2 provider.");
        String authorizationUrl = JwtValidationUtils.buildAuthRedirectUrl();
        throw new RedirectException(authorizationUrl);
    }
}
