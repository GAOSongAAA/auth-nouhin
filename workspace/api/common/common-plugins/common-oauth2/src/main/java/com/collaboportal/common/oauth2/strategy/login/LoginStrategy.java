package com.collaboportal.common.oauth2.strategy.login;

import com.collaboportal.common.strategy.AuthStrategy;

@FunctionalInterface
public interface LoginStrategy extends AuthStrategy {
    @Override
    void run(Object obj);
}
