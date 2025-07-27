package com.collaboportal.common.login.mapper;

import com.collaboportal.common.login.model.DTO.UserEPL;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 登录相关的MyBatis Mapper接口
 * 定义了从数据库获取用户信息的抽象方法。
 */
@Mapper
public interface LoginMapper {

    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 匹配的用户信息，如果不存在则返回null
     */
    UserEPL findUserByUsername(@Param("username") String username);

}
