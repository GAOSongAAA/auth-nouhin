import { ResponseBase } from './http'

/**
 * 詳細画面初期表示 API リクエスト
 * @property requestparameter  リクエストパラメーター
 */
export type GetRequest = {
  /** リクエストパラメーター */
  requestparameter: string
}

/**
 * 詳細画面初期表示 API レスポンス
 * @property pre_kkk_nm  企画名
 * @property pre_nhn_nm  納品先
 * @property pre_tok_cod  得意先コード
 * @property pre_dnp_ymd  伝票日付
 * @property pre_dnp_no  伝票No
 * @property pre_syh_kkk_yry  商品/規格容量
 * @property pre_syh_cod  商品コード
 * @property pre_hnb_num  数量
 * @property pre_dcf_cod  DCFコード
 * @property pre_ful_nm  処方元名(正式)
 * @property pre_nm  処方元名(略式)
 * @property pre_cod  処方元不明コード
 * @property pre_ryu  不明理由
 * @property pre_dr  処方医
 * @property pre_dr_cod  処方医不明コード
 * @property pre_dr_input_flg  処方医入力可否フラグ
 * @property pre_dp  診療科
 * @property pre_dp_cod  診療科不明コード
 * @property pre_mr_ren  MR連絡事項
 * @property pre_stat_flg  状態
 */
export type GetResponse = {
  /** 企画名 */
  pre_kkk_nm: string
  /** 納品先 */
  pre_nhn_nm: string
  /** 得意先コード */
  pre_tok_cod: string
  /** 伝票日付 */
  pre_dnp_ymd: string
  /** 伝票No */
  pre_dnp_no: string
  /** 商品/規格容量 */
  pre_syh_kkk_yry: string
  /** 商品コード */
  pre_syh_cod: string
  /** 数量 */
  pre_hnb_num: string
  /** DCFコード */
  pre_dcf_cod: string
  /** 処方元名(正式) */
  pre_ful_nm: string
  /** 処方元名(略式) */
  pre_nm: string
  /** 処方元不明コード */
  pre_cod: number
  /** 不明理由 */
  pre_ryu: string
  /** 処方医 */
  pre_dr: string
  /** 処方医不明コード */
  pre_dr_cod: number
  /** 処方医入力可否フラグ */
  pre_dr_input_flg: number
  /** 診療科 */
  pre_dp: string
  /** 診療科不明コード */
  pre_dp_cod: number
  /** MR連絡事項 */
  pre_mr_ren: string
  /** 状態 */
  pre_stat_flg: number
} & ResponseBase