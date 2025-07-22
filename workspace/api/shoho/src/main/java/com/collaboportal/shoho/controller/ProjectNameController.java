package com.collaboportal.shoho.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.shoho.service.ProjectNameService;

import jakarta.servlet.http.HttpServletRequest;

import com.collaboportal.shoho.model.ProjectNameRequestParameter;
import com.collaboportal.shoho.model.ProjectNameResponseBody;
import com.collaboportal.common.jwt.entity.JwtObject;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.shoho.entity.ProjectName;

import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping({ "/mr" })
public class ProjectNameController {

	private final ProjectNameService project_nameService;

	public ProjectNameController(ProjectNameService project_nameService) {
		this.project_nameService = project_nameService;
	}

	/**
	 * 企画名一覧取得メソッド
	 * 
	 * @param request HTTPリクエスト
	 * @return レスポンスボディ
	 */
	@GetMapping("/project_name")
	public ProjectNameResponseBody<List<ProjectName>> getProjectName(HttpServletRequest request,
			ProjectNameRequestParameter param) {

		// JWTから本部フラグ、メールアドレスを取得する
		JwtObject jwtObject = JwtTokenUtil.getItemsFromRequest(request);

		// 本部フラグがnullの場合は500エラー
		if (jwtObject.getHonbuFlg() == null) {
			throw new RuntimeException("AuthTokenが不正です。");
		}

		param.setHeadquartersFlag(jwtObject.getHonbuFlg());
		param.setEmail(jwtObject.getEmail());

		return new ProjectNameResponseBody<List<ProjectName>>(project_nameService.getProjectName(param));
	}
}
