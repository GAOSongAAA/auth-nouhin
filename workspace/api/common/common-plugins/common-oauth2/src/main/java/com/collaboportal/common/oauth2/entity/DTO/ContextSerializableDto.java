package com.collaboportal.common.oauth2.entity.DTO;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContextSerializableDto implements Serializable{

    String issuer;
    String clientId;
    String audience;

}
