package com.collaboportal.common.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtObject {
    
    //メール
    String email;


    //本部フラグ
    String honbuFlg;

}
