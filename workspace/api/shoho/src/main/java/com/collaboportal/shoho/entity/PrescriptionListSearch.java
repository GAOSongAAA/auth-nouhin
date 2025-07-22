package com.collaboportal.shoho.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

/**
 * 処方元情報一覧検索のエンティティクラスです.
 */
@Getter
@Setter
public class PrescriptionListSearch implements Serializable {

    /** 企画ID */
    private String shm_kkk_cod;

    /** 企画名 */
    private String shm_kkk_nm;

    /** 営業部 */
    private String shm_egb_nm;

    /** 支店 */
    private String shm_stn_nm;

    /** 課 */
    private String shm_ka_nm;

    /** 担当者 */
    private String shm_tts_nm;

    /** 伝票部No. */
    private String shm_dpy_bno;

    /** 伝票部No.(外部) */
    private String shm_dpy_kno_gb;

    /** 伝票No. */
    private String shm_dpy_no;

    /** 伝票行No. */
    private String shm_dpy_rno;

    /** 売上日 */
    private String shm_urg_ymd;

    /** 発売元 */
    private String shm_hbm_nm;

    /** 商品名/規格容量名 */
    private String shm_syh_kkk_yry_nm;

    /** 納品先コード */
    private String shm_nhn_cod;

    /** 納品先名 */
    private String shm_nhn_nm;

    /** 販売数 */
    private String shm_hnb_nm;

    /** DCFコード */
    private String shm_dcf_cod;

    /** 処方元名 */
    private String shm_nm;

    /** 処方医 */
    private String shm_dr_nm;

    /** MR担当者メールアドレス */
    private String shm_mail_adr_mr;

    /** 期限日 */
    private String shm_kkn_ymd;

    /** 状態 */
    private String shm_stat_cod;

    /** 入力日時 */
    private String shm_nrk_ymd;

    /** 更新日時 */
    private String shm_ksn_ymd;

    /** 総件数 */
    @JsonIgnore
    private Integer all_su;
}
