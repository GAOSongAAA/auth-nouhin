package com.collaboportal.common.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserMaster {
    // **本部フラグ */
    private String userType;

    private String userMail;
}
