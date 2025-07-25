package com.collaboportal.common.spring.strategy.auth;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.RedirectException;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.strategy.auth.AuthenticationStrategy;
import com.collaboportal.common.utils.Message;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("databaseAuthStrategy")
public class DatabaseAuthStrategy implements AuthenticationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthStrategy.class);
    private final JwtTokenUtil jwtTokenUtil;

    public DatabaseAuthStrategy(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException, RedirectException {
        logger.debug("Executing Database Authentication Strategy...");

        String token = request.getCookieValue(Message.Cookie.AUTH);

        if (token == null || token.isEmpty()) {
            logger.info("Auth_Token not found in cookie. Redirecting to login page.");
            throw new RedirectException("/login");
        }

        try {
            if (jwtTokenUtil.isTokenExpired(token)) {
                logger.info("Auth_Token has expired. Redirecting to login page.");
                throw new RedirectException("/login");
            }

            Map<String, String> userInfo = JwtTokenUtil.getItemsJwtToken(token);
            if (userInfo.isEmpty()) {
                logger.warn("Token is valid but contains no user information.");
                throw new AuthenticationException("Invalid token: user information is missing.");
            }
            CommonHolder.getStorage().set("USER_INFO", userInfo);
            logger.debug("User information parsed and stored in request context: {}", userInfo.get("sub"));

            String refreshedToken = jwtTokenUtil.updateExpiresAuthToken(token);
            response.addCookie(Message.Cookie.AUTH, refreshedToken, "/", null, -1);
            logger.debug("Auth_Token has been refreshed and set in response cookie.");

            logger.info("Database authentication successful for user: {}.", userInfo.get("sub"));

        } catch (ExpiredJwtException e) {
            logger.info("Auth_Token has expired (caught). Redirecting to login page.");
            throw new RedirectException("/login");
        } catch (Exception e) {
            logger.error("An error occurred during database token validation. Redirecting to login page.", e);
            throw new RedirectException("/login");
        }
    }
}
