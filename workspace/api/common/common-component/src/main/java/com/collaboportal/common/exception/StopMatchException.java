package com.collaboportal.common.exception;

public class StopMatchException extends RuntimeException {

    public StopMatchException() {
        super("マッチングを停止しました。");
    }
    public StopMatchException(String message) {
        super(message);
    }
}
