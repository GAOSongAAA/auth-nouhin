package com.collaboportal.common.oauth2.entity.DTO;

import com.collaboportal.common.jwt.entity.UserMaster;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMasterCollabo implements UserMaster {

    private String userId;
    private String userMail;
    private String userType;

}
