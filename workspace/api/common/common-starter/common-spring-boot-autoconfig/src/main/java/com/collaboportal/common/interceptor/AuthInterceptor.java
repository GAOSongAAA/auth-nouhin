package com.collaboportal.common.interceptor;


import org.springframework.web.servlet.HandlerInterceptor;

import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.ParamFunction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AuthInterceptor implements HandlerInterceptor {

    public ParamFunction<Object> auth = handler -> {};

    public AuthInterceptor() {
	}
    public AuthInterceptor(ParamFunction<Object> auth) {
		this.auth = auth;
	}
    public AuthInterceptor setAuth(ParamFunction<Object> auth) {
		this.auth = auth;
		return this;
	}

    @Override
	@SuppressWarnings("all")
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		try {
			
			// Auth 校验  
			auth.run(handler);
			
		} catch (StopMatchException e) {
			// StopMatchException 异常代表：停止匹配，进入Controller

		} catch (BackResultException e) {
			// BackResultException 异常代表：停止匹配，向前端输出结果
			// 		请注意此处默认 Content-Type 为 text/plain，如果需要返回 JSON 信息，需要在 back 前自行设置 Content-Type 为 application/json
			// 		例如：SaHolder.getResponse().setHeader("Content-Type", "application/json;charset=UTF-8");
			if(response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8"); 
			}
			response.getWriter().print(e.getMessage());
			return false;
		}
		
		// 通过验证 
		return true;
	}
}
