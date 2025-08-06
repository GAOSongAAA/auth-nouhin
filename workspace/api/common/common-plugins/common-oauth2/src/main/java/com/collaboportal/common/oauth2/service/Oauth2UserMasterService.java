package com.collaboportal.common.oauth2.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.collaboportal.common.oauth2.entity.DTO.UserMasterCollabo;
import com.collaboportal.common.oauth2.repository.Oauth2Mapper;
import com.collaboportal.common.utils.AbstractMasterLoader;

@Service
public class Oauth2UserMasterService extends AbstractMasterLoader<UserMasterCollabo> {

    private final Oauth2Mapper oauth2Mapper;

    public Oauth2UserMasterService(Oauth2Mapper oauth2Mapper) {
        super(null, null);
        this.oauth2Mapper = oauth2Mapper;
    }

    @Override
    public UserMasterCollabo loadByEmail(String email) {

        return Optional.ofNullable(oauth2Mapper.findUserMasterByEmail(email))
                .orElseGet(() -> UserMasterCollabo.builder()
                        .userMail(email)
                        .userType("0")
                        .userId("0")
                        .build());
    }
}
