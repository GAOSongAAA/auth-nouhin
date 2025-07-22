package com.collaboportal.shoho.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.collaboportal.shoho.entity.ProjectName;
import com.collaboportal.shoho.model.ProjectNameRequestParameter;

@Mapper
public interface ProjectNameMapper {

     /**
      * 企画名一覧取得メソッド
      * 
      * @param param 企画名一覧取得パラメータ
      * @return 企画名一覧のリスト
      */
     List<ProjectName> getProjectName(ProjectNameRequestParameter param);

}
