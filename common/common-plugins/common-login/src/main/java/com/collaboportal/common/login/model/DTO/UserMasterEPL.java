package com.collaboportal.common.login.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.collaboportal.common.jwt.entity.UserMaster;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMasterEPL implements UserMaster {

    private String userType;
    private String userMail;

    private String password;

    private String userId;
}
