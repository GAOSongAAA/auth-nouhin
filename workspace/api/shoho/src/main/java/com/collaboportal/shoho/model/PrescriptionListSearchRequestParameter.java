package com.collaboportal.shoho.model;

import java.util.Optional;

import com.collaboportal.common.jwt.entity.JwtObject;
import com.collaboportal.common.utils.BaseRequestParameter;
import com.collaboportal.common.utils.Message;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * 処方元情報一覧検索のRequestParameterクラスです.
 */
@Setter
@Getter
public class PrescriptionListSearchRequestParameter extends BaseRequestParameter {

    /** 企画ID */
    @Pattern(regexp = Message.VALIDATION_ONLY_HALF_NUMERIC)
    private String kkk_cod;

    /** DCFコード */
    @Pattern(regexp = Message.VALIDATION_ALPHA_NUMERIC)
    private String dcf_cod;

    /** 売上日 開始日 */
    @Pattern(regexp = Message.VALIDATION_ONLY_HALF_NUMERIC)
    private String urg_ymd_stt;

    /** 売上日 終了日 */
    @Pattern(regexp = Message.VALIDATION_ONLY_HALF_NUMERIC)
    private String urg_ymd_end;

    /** ログイン情報 */
    private JwtObject jwtObject;

    /**
     * 処方医入力否数フラグを取得するか判定します.<br>
     * オフセットが1の場合{@code true}を返します.
     * 
     * @return 結果
     *         <ul>
     *         <li>{@code true} :処方医入力否数フラグを取得する.</li>
     *         <li>{@code false}:処方医入力否数フラグを取得しない.</li>
     *         </ul>
     */
    public boolean shouldFetchFlag() {
        return offset.equals(1);
    }

    /**
     * バリデーションチェックを実行します.
     * <ul>
     * <li>本部フラグのチェック</li>
     * <li>mailアドレスのチェック</li>
     * </ul>
     * 
     * @throws RuntimeException
     *                          <ul>
     *                          <li>本部フラグが不正の場合</li>
     *                          <li>mailアドレスが不正の場合</li>
     *                          </ul>
     */
    public void valid() throws RuntimeException {
        // 本部フラグのチェック.
        // nullではないこと.
        String honbuFlg = Optional.ofNullable(jwtObject.getHonbuFlg())
                .orElseThrow(() -> new RuntimeException("AuthTokenが不正です。"));

        // mailアドレスのチェック.
        // nullでないこと(MR担当の場合).
        if (honbuFlg.equals("0")) {
            Optional.ofNullable(jwtObject.getEmail())
                    .orElseThrow(() -> new RuntimeException("AuthTokenが不正です。"));
        }
    }
}
