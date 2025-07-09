package com.collaboportal.common.oauth2.service;

import com.collaboportal.common.oauth2.entity.DTO.IUserInfoDto;

public interface IUserInfoService<T extends IUserInfoDto> {

    T loadByEmail(String email);

    T loadById(String userId);

}
