package com.collaboportal.common.oauth2.strategy.login;

import com.collaboportal.common.context.CallbackContext;
import com.collaboportal.common.strategy.AuthStrategy;

@FunctionalInterface
public interface LoginStrategy extends AuthStrategy<CallbackContext> {
    @Override
    void login(CallbackContext context);
}
