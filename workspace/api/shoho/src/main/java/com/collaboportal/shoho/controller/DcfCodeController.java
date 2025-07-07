package com.collaboportal.shoho.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.shoho.service.DcfCodeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import com.collaboportal.common.jwt.entity.JwtObject;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.shoho.model.DcfCodeRequestParameter;
import com.collaboportal.shoho.model.DcfCodeResponseBody;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping({ "/mr" })
public class DcfCodeController {

	private final DcfCodeService project_nameService;

	public DcfCodeController(DcfCodeService project_nameService) {
		this.project_nameService = project_nameService;
	}

	/**
	 * DCFコード一覧取得メソッド
	 * 
	 * @param request HTTPリクエスト
	 * @return レスポンスボディ
	 */
	@GetMapping("/dcf_code")
	public DcfCodeResponseBody getDcfCode(HttpServletRequest request, @Valid DcfCodeRequestParameter param) {

		// JWTからメールアドレスを取得する
		JwtObject jwtObject = JwtTokenUtil.getItemsFromRequest(request);

		param.setEmail(jwtObject.getEmail());

		return new DcfCodeResponseBody(project_nameService.getDcfCode(param));
	}
}
