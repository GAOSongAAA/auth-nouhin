package com.collaboportal.common.strategy;



@FunctionalInterface
public interface AuthStrategy {
    void run(Object obj);
}
