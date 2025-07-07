import { ResponseBase } from './http'

/**
 * 企画名一覧取得 API リクエスト
 */
export type GetRequest = {
  /* DCFコード */
  dcf_cod: string
}

/**
 * 企画名一覧取得 API レスポンス
 * @property lst_kkk_inf 企画情報
 */
export type GetResponse = {
  lst_kkk_inf: Array<KikakuInfo>
} & ResponseBase

/**
 * 企画名情報
 * @property kkk_cod 企画ID
 * @property kkk_nm 企画名
 */
export type KikakuInfo = {
  kkk_cod: string
  kkk_nm: string
}
