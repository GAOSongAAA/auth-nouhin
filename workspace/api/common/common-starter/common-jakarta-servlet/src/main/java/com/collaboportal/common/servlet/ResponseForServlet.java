package com.collaboportal.common.servlet;

import com.collaboportal.common.context.model.BaseResponse;
import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;

import jakarta.servlet.http.HttpServletResponse;

public class ResponseForServlet implements BaseResponse{

    protected HttpServletResponse response;

    public ResponseForServlet(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public Object getSource() {
        return response;
    }

	@Override
	public BaseResponse setStatus(int sc) {
		response.setStatus(sc);
		return this;
	}

    @Override
    public BaseResponse setHeader(String name, String value) {
        response.setHeader(name, value);
        return this;
    }

    @Override
    public BaseResponse addHeader(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

	@Override
	public Object redirect(String url) {
		try {
			response.sendRedirect(url);
		} catch (Exception e) {
			throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
		}
		return null;
	}

}
