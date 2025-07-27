package com.collaboportal.common.oauth2.strategy.login;



@FunctionalInterface
public interface LoginStrategy {
    
    void run(Object obj);
}
