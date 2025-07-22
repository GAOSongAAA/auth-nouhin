import { ResponseBase } from './http'

/**
 * 一覧検索 API リクエスト
 * @property limit ページ行数
 * @property offset オフセット
 * @property kkk_cod 企画ID
 * @property dcf_cod DCFコード
 * @property urg_ymd_stt 売上日　開始日
 * @property urg_ymd_end 売上日　終了日
 */
export type GetRequest = {
  /** ページ行数 */
  limit: number
  /** オフセット */
  offset: number
  /** 企画ID */
  kkk_cod: string
  /** DCFコード */
  dcf_cod: string 
  /** 売上日　開始日 */
  urg_ymd_stt: string
  /** 売上日　終了日 */
  urg_ymd_end: string
}

/**
 * 一覧検索 API レスポンス
 * @property all_su 総件数
 * @property sta_row 開始行
 * @property end_row 終了行
 * @property shm_inf 処方元情報リスト
 * @property shm_dr_input_disabled_flg 処方医入力有無フラグ
 */
export type GetResponse = {
  /** 総件数 */
  all_su: number
  /** 開始行 */
  sta_row: number
  /** 終了行 */
  end_row: number
  /** 処方元情報リスト */
  shm_inf: Array<PrescriptionInfo>
  /** 処方医入力有無フラグ */
  shm_dr_input_disabled_flg: number
} & ResponseBase

/**
 * 処方元情報
 * @property shm_kkk_cod 企画ID
 * @property shm_kkk_nm 企画名
 * @property shm_egb_nm 営業部
 * @property shm_stn_nm 支店
 * @property shm_ka_nm 課
 * @property shm_tts_nm 得意先担当者
 * @property shm_dpy_bno 伝票部No.
 * @property shm_dpy_kno_gb 伝票課No.(外部)
 * @property shm_dpy_no 伝票No.
 * @property shm_dpy_rno 伝票行No.
 * @property shm_urg_ymd 売上日
 * @property shm_hbm_nm 発売元
 * @property shm_syh_nm 商品名
 * @property shm_syh_kkk_yry_nm 商品規格/容量
 * @property shm_nhn_cod 納品先コード
 * @property shm_nhn_nm 納品先名
 * @property shm_hnb_nm 販売数
 * @property shm_nm 処方元名
 * @property shm_dcf_cod DCFコード
 * @property shm_dr_nm 処方医
 * @property shm_mail_adr_mr MR担当者メールアドレス
 * @property shm_kkn_ymd 期限日
 * @property shm_stat_cod 状態
 * @property shm_nrk_ymd 入力日時
 * @property shm_ksn_ymd 更新日時
 */
export type PrescriptionInfo = {
  /** 企画ID */
  shm_kkk_cod: string
  /** 企画名 */
  shm_kkk_nm: string
  /** 営業部 */
  shm_egb_nm: string
  /** 支店 */
  shm_stn_nm: string
  /** 課 */
  shm_ka_nm: string
  /** 得意先担当者 */
  shm_tts_nm: string
  /** 伝票部No. */
  shm_dpy_bno: string
  /** 伝票課No.(外部) */
  shm_dpy_kno_gb: string
  /** 伝票No. */
  shm_dpy_no: string
  /** 伝票行No. */
  shm_dpy_rno: string
  /** 売上日 */
  shm_urg_ymd: string
  /** 発売元 */
  shm_hbm_nm: string
  /** 商品名 */
  shm_syh_nm: string
  /** 商品規格/容量 */
  shm_syh_kkk_yry_nm: string
  /** 納品先コード */
  shm_nhn_cod: string
  /** 納品先名 */
  shm_nhn_nm: string
  /** 販売数 */
  shm_hnb_nm: string
  /** 処方元名 */
  shm_nm: string
  /** DCFコード */
  shm_dcf_cod: string
  /** 処方医 */
  shm_dr_nm: string
  /** MR担当者メールアドレス */
  shm_mail_adr_mr: string
  /** 期限日 */
  shm_kkn_ymd: string
  /** 状態 */
  shm_stat_cod: string
  /** 入力日時 */
  shm_nrk_ymd: string
  /** 更新日時 */
  shm_ksn_ymd: string
}
