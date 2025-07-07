package com.collaboportal.shoho.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import lombok.Data;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DcfCode implements Serializable {
    /** DCFコード */
    private String dcf_cod;
}