package com.collaboportal.common.jwt.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.collaboportal.common.jwt.entity.UserMaster;
import com.collaboportal.common.jwt.repository.AuthMapper;
import com.collaboportal.common.utils.AbstractMasterLoader;

/**
 * ユーザーマスタ詳細サービスクラス
 * ユーザーマスタ情報の取得を提供する
 */
@Service
public class JwtUserMasterDetailsService extends AbstractMasterLoader<UserMaster> {

    private final AuthMapper authMapper;

    /**
     * コンストラクタ
     * 
     * @param authMapper 認証マッパー
     */
    public JwtUserMasterDetailsService(AuthMapper authMapper) {
        super(null, null); 
        this.authMapper = authMapper;
    }
    
    /**
     * loadByEmailメソッドをオーバーライドし、emailをフォールバック値として使用する
     * @param email メールアドレス
     * @return ユーザーマスタまたはフォールバック値
     */
    @Override
    public UserMaster loadByEmail(String email) {
        return Optional.ofNullable(authMapper.findUserMasterByEmail(email))
            .orElse(UserMaster.builder()
                .UserMail(email)
                .userType("0")
                .build());
    }
}
