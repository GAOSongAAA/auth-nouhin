package com.collaboportal.common.jwt.entity;

/**
 * ユーザー共通情報の最小インタフェース
 */
public interface UserMaster {

    /** メールアドレス */
    String getUserMail();

    String getUserId();
}
