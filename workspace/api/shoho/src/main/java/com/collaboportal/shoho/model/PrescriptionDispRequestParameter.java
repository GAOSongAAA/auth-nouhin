package com.collaboportal.shoho.model;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 処方元情報の詳細画面初期表示のリクエストModelクラスです.
 */
@Getter
@Setter
public class PrescriptionDispRequestParameter {

    /**
     * リクエストパラメータ
     */
    @NotNull
    private String requestparameter;

    /**
     * リクエストパラメータ構造
     */
    private PrescriptionDispRequestParameter.Pram param;

    /**
     * リクエストパラメータを解決します.
     * 
     * @throws RuntimeException
     */
    public void resolve() throws RuntimeException {
        try {
            // デコード
            String decoded = new String(
                    Base64.getUrlDecoder().decode(requestparameter),
                    StandardCharsets.UTF_8);

            // マッピング
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, true);
            PrescriptionDispRequestParameter.Pram param = mapper.readValue(
                    decoded,
                    PrescriptionDispRequestParameter.Pram.class);
            this.param = param;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("クッキー値のパースに失敗しました.: " + e.getMessage(), e);
        }
    }

    /**
     * リクエストパラメータ構造を定義するクラスです.
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Pram {
        /** 伝票部No. */
        private String dpy_bno;

        /** 伝票課No. */
        private String dpy_kno;

        /** 伝票No. */
        private String dpy_no;

        /** 伝票行No. */
        private String dpy_lno;

        /** 売上計上月日 */
        private String uriage_ymd;

        @JsonCreator
        public Pram(
                @JsonProperty(value = "DPY_BNO", required = true) String dpy_bno,
                @JsonProperty(value = "DPY_KNO", required = true) String dpy_kno,
                @JsonProperty(value = "DPY_NO", required = true) String dpy_no,
                @JsonProperty(value = "DPY_LNO", required = true) String dpy_lno,
                @JsonProperty(value = "URIAGE_YMD", required = true) String uriage_ymd) {
            this.dpy_bno = dpy_bno;
            this.dpy_kno = dpy_kno;
            this.dpy_no = dpy_no;
            this.dpy_lno = dpy_lno;
            this.uriage_ymd = uriage_ymd;
        }
    }
}