package com.collaboportal.common.jwt.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.collaboportal.common.jwt.entity.UserMaster;

@Mapper
public interface AuthMapper {
        /**
         * ユーザーマスタを取得する
         * 
         * @param userMail
         * @return
         */
        UserMaster findUserMasterByEmail(@Param("userMail") String userMail);


}
