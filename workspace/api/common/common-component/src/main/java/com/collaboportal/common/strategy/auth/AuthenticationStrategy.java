package com.collaboportal.common.strategy.auth;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.RedirectException;

@FunctionalInterface
public interface AuthenticationStrategy {
    void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException, RedirectException;
}
