package com.collaboportal.common.login.handler;

import com.collaboportal.common.login.context.LoginContext;

@FunctionalInterface
public interface LoginHanlder {

    boolean handle(LoginContext context);

}
