package com.collaboportal.common.login.service.impl;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.login.mapper.LoginMapper;
import com.collaboportal.common.login.model.DTO.UserEPL;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.service.LoginAttemptService;
import com.collaboportal.common.login.service.LoginService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录服务的实现类
 * 实现了核心的登录逻辑，包括密码验证、失败锁定和JWT生成。
 */
@Service
public class LoginServiceImpl implements LoginService {

    private final LoginMapper loginMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final LoginAttemptService loginAttemptService;

    /**
     * 构造函数
     * 通过依赖注入初始化所需的服务和组件。
     *
     * @param loginMapper        用于数据库操作的MyBatis Mapper
     * @param passwordEncoder   用于密码加密和验证的服务
     * @param jwtTokenUtil      用于生成JWT的工具类
     * @param loginAttemptService 用于处理登录失败的服务
     */
    public LoginServiceImpl(LoginMapper loginMapper, PasswordEncoder passwordEncoder,
                           JwtTokenUtil jwtTokenUtil, LoginAttemptService loginAttemptService) {
        this.loginMapper = loginMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.loginAttemptService = loginAttemptService;
    }

    /**
     * 用户登录认证
     * 1. 检查账户是否被锁定。
     * 2. 从数据库获取用户信息。
     * 3. 验证密码。
     * 4. 登录成功后，生成并返回JWT。
     * 5. 登录失败后，记录失败次数。
     *
     * @param loginRequest 包含用户凭证的登录请求
     * @return 包含JWT的登录响应
     * @throws AuthenticationException 如果认证失败
     */
    @Override
    public void login(LoginRequest loginRequest) {
        BaseResponse response = CommonHolder.getResponse();
        // 检查账户是否被锁定
        if (loginAttemptService.isBlocked(loginRequest.getUsername())) {
            throw new AuthenticationException("Account is blocked");
        }

        // 从数据库获取用户信息
        UserEPL user = loginMapper.findUserByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new AuthenticationException("Invalid credentials");
        }

        // 验证密码
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            // 登录成功，重置失败计数
            loginAttemptService.loginSucceeded(loginRequest.getUsername());

            // 生成JWT
            String token = jwtTokenUtil.generateTokenFromObject(user);
            CookieUtil.setNoneSameSiteCookie(response, "token", token);
            response.redirect("/index.html");
        } else {
            // 登录失败，增加失败计数
            loginAttemptService.loginFailed(loginRequest.getUsername());
            throw new AuthenticationException("Invalid credentials");
        }
    }
}
