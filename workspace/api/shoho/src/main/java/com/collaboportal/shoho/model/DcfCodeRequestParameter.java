package com.collaboportal.shoho.model;

import com.collaboportal.common.utils.Message;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DcfCodeRequestParameter {

    /** メールアドレス(JWT) */
    private String email;

    /** 企画ID */
    @Pattern(regexp = Message.VALIDATION_ONLY_HALF_NUMERIC)
    private String kkk_cod;

    public DcfCodeRequestParameter(String email, String kkk_cod) {
        this.email = email;
        this.kkk_cod = kkk_cod;
    }
}