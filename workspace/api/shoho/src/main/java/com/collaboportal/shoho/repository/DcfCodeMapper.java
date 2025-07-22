package com.collaboportal.shoho.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.collaboportal.shoho.entity.DcfCode;
import com.collaboportal.shoho.model.DcfCodeRequestParameter;

@Mapper
public interface DcfCodeMapper {

     /**
      * DCFコード一覧取得メソッド
      * 
      * @return DCFコード一覧のリスト
      */
     List<DcfCode> getDcfCode(DcfCodeRequestParameter param);
}
