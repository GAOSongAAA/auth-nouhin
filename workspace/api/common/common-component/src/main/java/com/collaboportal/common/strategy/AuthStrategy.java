package com.collaboportal.common.strategy;

@SuppressWarnings("rawtypes")
@FunctionalInterface
public interface AuthStrategy<T> {
    void login(T context);
}
