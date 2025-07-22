import { ResponseBase } from './http'

/**
 * DCFコード一覧取得 API リクエスト
 */
export type GetRequest = {
  /* 企画コード */
  kkk_cod: string
}

/**
 * DCFコード一覧取得 API レスポンス
 * @property dcf_cod DCFコード
 */
export type GetResponse = {
  dcf_cod: string[]
} & ResponseBase
