package com.collaboportal.shoho.service;

import java.util.List;

import com.collaboportal.shoho.entity.ProjectName;
import com.collaboportal.shoho.model.ProjectNameRequestParameter;

public interface ProjectNameService {

     /**
      * 企画名一覧取得メソッド
      * 
      * @param param 企画名一覧取得パラメータ
      * @return 企画名一覧のリスト
      */
     List<ProjectName> getProjectName(ProjectNameRequestParameter param);
}
