package com.collaboportal.common.oauth2.entity.DTO;

public class UserInfo implements IUserInfoDto {

    private String userId;
    private String email;

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
