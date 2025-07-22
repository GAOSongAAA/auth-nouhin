package com.collaboportal.shoho.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Data;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectName implements Serializable {
    /** 企画ID */
    private String kkk_cod;

    /** 企画名 */
    private String kkk_nm;
}