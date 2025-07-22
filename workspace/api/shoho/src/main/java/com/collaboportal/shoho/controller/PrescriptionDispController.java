package com.collaboportal.shoho.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.shoho.model.PrescriptionDispRequestParameter;
import com.collaboportal.shoho.model.PrescriptionDispResponseBody;
import com.collaboportal.shoho.service.PrescriptionDispService;

import lombok.RequiredArgsConstructor;

/**
 * 処方元情報の詳細画面初期表示のControllerクラスです.
 */
@RestController
@RequestMapping("/mr/prescription_disp")
@RequiredArgsConstructor
public class PrescriptionDispController {

	/** ロガー */
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** サービス */
	private final PrescriptionDispService prescriptionDispService;

	/**
	 * 処方元情報の詳細画面初期表示データを取得します.
	 * 
	 * @param param パラメータ
	 * 
	 * @return {@link PrescriptionDispResponseBody} レスポンスボディ
	 */
	@GetMapping()
	public PrescriptionDispResponseBody refer(
			@Valid PrescriptionDispRequestParameter param) {

		// リクエストパラメータを解析します.
		param.resolve();

		// ログ出力
		dump(param.getParam());

		// サービスを呼び出す
		return PrescriptionDispResponseBody.of(prescriptionDispService.refer(param));
	}

	/**
	 * パラメータをログにダンプします.
	 * 
	 * @param param パラメータ
	 */
	private void dump(PrescriptionDispRequestParameter.Pram param) {
		StringBuilder sb = new StringBuilder("\n")
				.append("===== REQUEST_PARAM =====").append("\n")
				.append(String.format("dpy_bno    : %s", param.getDpy_bno())).append("\n")
				.append(String.format("dpy_kno    : %s", param.getDpy_kno())).append("\n")
				.append(String.format("dpy_no     : %s", param.getDpy_no())).append("\n")
				.append(String.format("dpy_lno    : %s", param.getDpy_lno())).append("\n")
				.append(String.format("uriage_ymd : %s", param.getUriage_ymd())).append("\n")
				.append("=========================");

		logger.info(sb.toString());
	}
}
