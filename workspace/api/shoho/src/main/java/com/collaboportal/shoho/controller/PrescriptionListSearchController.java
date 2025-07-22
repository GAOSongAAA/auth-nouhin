package com.collaboportal.shoho.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.shoho.model.PrescriptionListSearchRequestParameter;
import com.collaboportal.shoho.model.PrescriptionListSearchResponseBody;
import com.collaboportal.shoho.service.PrescriptionListSearchService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 処方元情報一覧検索のControllerクラスです.
 */
@RestController
@RequestMapping("/mr/prescriptionList_search")
@RequiredArgsConstructor
public class PrescriptionListSearchController {

    /**
     * ログ
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * サービス
     */
    private final PrescriptionListSearchService prescriptionListSearchService;

    /**
     * 処方元情報を参照し、対象データを取得します.
     * 
     * @param request リクエスト
     * @param param   一覧検索パラメータ
     *
     * @return {@link PrescriptionListSearchResponseBody} 一覧検索レスポンスボディ
     */
    @GetMapping()
    public PrescriptionListSearchResponseBody refer(
            HttpServletRequest request,
            @Valid PrescriptionListSearchRequestParameter param) {

        // ログイン情報セット
        param.setJwtObject(JwtTokenUtil.getItemsFromRequest(request));

        // バリデーションチェック実行.
        param.valid();

        // ログ出力
        dump(param);

        // サービス呼び出し
        // 処方元情報一覧を取得する
        return PrescriptionListSearchResponseBody.of(
                prescriptionListSearchService.refer(param),
                param.getOffset(),
                param.getLimit());
    }

    /**
     * パラメータをログにダンプします.
     * 
     * @param param パラメータ
     */
    private void dump(PrescriptionListSearchRequestParameter param) {
        StringBuilder sb = new StringBuilder("\n")
                .append("===== REQUEST_PARAM =====").append("\n")
                .append(String.format("kkk_cod      : %s", param.getKkk_cod())).append("\n")
                .append(String.format("dcf_cod      : %s", param.getDcf_cod())).append("\n")
                .append(String.format("urg_ymd_stt  : %s", param.getUrg_ymd_stt())).append("\n")
                .append(String.format("urg_ymd_end  : %s", param.getUrg_ymd_end())).append("\n")
                .append("====== LOGIN_INFO =======").append("\n")
                .append(String.format("email        : %s", param.getJwtObject().getEmail())).append("\n")
                .append(String.format("honbuFlg     : %s", param.getJwtObject().getHonbuFlg())).append("\n")
                .append("=========================");

        logger.info(sb.toString());
    }
}
