package com.collaboportal.common.interceptor;

import java.util.function.Function;

import org.springframework.web.servlet.HandlerInterceptor;

import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.funcs.ParamFunction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

	public ParamFunction<Object> auth = handler -> {
	};

	public Function<Object, Void> handleFunction;

	public AuthInterceptor() {
	}

	public AuthInterceptor(ParamFunction<Object> auth) {
		this.auth = auth;
	}

	public AuthInterceptor(Function<Object, Void> handleFunction) {
		this.handleFunction = handleFunction;
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
			auth.run(handler);

		} catch (StopMatchException e) {

		} catch (BackResultException e) {
			if (response.getContentType() == null) {
				response.setContentType("text/plain; charset=utf-8");
			}
			response.getWriter().print(e.getMessage());
			return false;
		}

		return true;
	}
}
