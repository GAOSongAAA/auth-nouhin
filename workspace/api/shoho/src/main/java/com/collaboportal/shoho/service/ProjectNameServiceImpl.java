package com.collaboportal.shoho.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.shoho.entity.ProjectName;
import com.collaboportal.shoho.model.ProjectNameRequestParameter;
import com.collaboportal.shoho.repository.ProjectNameMapper;

@Service
public class ProjectNameServiceImpl implements ProjectNameService {

	private final ProjectNameMapper projectNameMapper;

	public ProjectNameServiceImpl(ProjectNameMapper projectNameMapper) {
		this.projectNameMapper = projectNameMapper;
	}

	/**
	 * 企画名一覧取得メソッド
	 * 
	 * @param param 企画名一覧取得パラメータ
	 * @return 企画名一覧のリスト
	 */
	@Override
	public List<ProjectName> getProjectName(ProjectNameRequestParameter param) {
		List<ProjectName> projectName = projectNameMapper.getProjectName(param);

		if (projectName == null || projectName.size() == 0) {
			throw new CommonException(InternalErrorCode.RECORD_NOT_FOUND_ERROR);
		}

		return projectName;
	}
}
