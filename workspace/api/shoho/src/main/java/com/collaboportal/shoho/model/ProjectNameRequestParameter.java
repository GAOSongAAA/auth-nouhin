package com.collaboportal.shoho.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProjectNameRequestParameter {

    /** 本部フラグ(JWT) */
    private String headquartersFlag;

    /** メールアドレス(JWT) */
    private String email;

    /** DCFコード */
    private String dcf_cod;

    public ProjectNameRequestParameter(String headquartersFlag, String email, String dcf_cod) {
        this.headquartersFlag = headquartersFlag;
        this.email = email;
        this.dcf_cod = dcf_cod;
    }
}