package com.collaboportal.common.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.application.ApplicationInfo;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestForServlet implements BaseRequest{


    protected HttpServletRequest request;

    public RequestForServlet(HttpServletRequest request) {
		this.request = request;
	}

    @Override
    public Object getSource() {
      return request;
    }

    @Override
    public String getParam(String name) {
        return request.getParameter(name);
    }

    @Override
	public Collection<String> getParamNames(){
		return Collections.list(request.getParameterNames());
	}

    
	@Override
	public Map<String, String> getParamMap(){
		// 获取所有参数
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String> map = new LinkedHashMap<>(parameterMap.size());
		for (String key : parameterMap.keySet()) {
			String[] values = parameterMap.get(key);
			map.put(key, values[0]);
		}
		return map;
	}
	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

    @Override
    public String getCookieValue(String name) {
        return request.getCookies() == null ? null : Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(name))
				.findFirst()
				.map(Cookie::getValue)
				.orElse(null);
    }

	@Override
	public String getCookieFirstValue(String name){
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	@Override
	public String getCookieLastValue(String name){
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && name.equals(cookie.getName())) {
					value = cookie.getValue();
				}
			}
		}
		return value;
	}

	@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getRequestURI());
	}

	@Override
	public String getUrl() {
		String currDomain = ConfigManager.getConfig().getCurrDomain();
		if( ! StringUtils.isEmpty(currDomain)) {
			return currDomain + this.getRequestPath();
		}
		return request.getRequestURL().toString();
	}
	

	@Override
	public String getHost() {
		return request.getServerName();
	}

	@Override
	public Object forward(String path) {
		try {
			HttpServletResponse response = (HttpServletResponse)ConfigManager.getCommonContext().getResponse().Source();
			request.getRequestDispatcher(path).forward(request, response);
			return null;
		} catch (ServletException | IOException e) {
			throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
		}
	}

    @Override
	public String getMethod() {
		return request.getMethod();
	}
}
