package com.collaboportal.common.strategy;


@FunctionalInterface
public interface AuthErrorStrategy {
    Object run(Throwable obj);
}
