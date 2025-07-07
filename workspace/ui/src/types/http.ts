/**
 * APIリクエストベース 型定義
 */
export type RequestBase = {
}

/**
 * エラーコード
 * ※正数→APIから返るエラーコード、負数→AxiosErrorを丸めたエラーコード
 */
export type ErrorCode = '200'| '302' | '400' | '401' | '403' | '409' | '404' | '500' |'503'| '-1' | '-2' | '-3'

/**
 * APIレスポンスベース 型定義
 * @property nb_err_cod - 内部エラーコード
 * @property err_msg - エラーメッセージ
 * @property err_level - エラーレベル
 */
export type ResponseBase = {
  nb_err_cod: ErrorCode
  err_msg: string
  err_level: string
}
