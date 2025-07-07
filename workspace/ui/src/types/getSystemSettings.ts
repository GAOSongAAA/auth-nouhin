import { ResponseBase } from "./http";

/**
 * システム設定値取得 API リクエスト
 */
export type GetRequest = {}

/**
 * システム設定値取得 API レスポンス
 */
export type GetResponse = {
  pass_change_url: string
  logout_url: string
} & ResponseBase