package com.collaboportal.shoho.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * 処方元情報の詳細画面初期表示の結果を格納するEntityクラスです.
 */
@Getter
@Setter
public class PrescriptionDispResult implements Serializable {

    /** 企画名 */
    private String pre_kkk_nm;

    /** 納品先 */
    private String pre_nhn_nm;

    /** 得意先コード */
    private String pre_tok_cod;

    /** 伝票日付 */
    private String pre_dnp_ymd;

    /** 伝票No */
    private String pre_dnp_no;

    /** 商品名/商品/規格容量 */
    private String pre_syh_kkk_yry;

    /** 商品コード */
    private String pre_syh_cod;

    /** 数量 */
    private String pre_hnb_num;

    /** DCFコード */
    private String pre_dcf_cod;

    /** 処方元名 */
    private String pre_nm;

    /** 正式処方元名 */
    private String pre_ful_nm;

    /** 処方元不明コード */
    private int pre_cod;

    /** 不明理由 */
    private String pre_ryu;

    /** 処方医 */
    private String pre_dr;

    /** 処方医不明コード */
    private int pre_dr_cod;

    /** 処方医入力可否フラグ */
    private int pre_dr_input_flg;

    /** 診療科 */
    private String pre_dp;

    /** 診療科不明コード */
    private int pre_dp_cod;

    /** MR連絡事項 */
    private String pre_mr_ren;

    /** 状態 */
    private int pre_stat_flg;
}
