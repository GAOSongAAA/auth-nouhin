import router from '@/router'
import { RequestBase, ResponseBase } from '@/types/http'
import axios from 'axios'
import { Http } from '../interface/http'
import { getAuth, getXsrfToken } from '@/util/cookies'
import { setParams } from '@/util/errCodeParam'
import * as Names from '@/router/names'

/** 認証情報 */
const AUTHORIZATION = 'Authorization'
/** CSRF対策トークン */
const X_XSRF_TOKEN = 'X-XSRF-TOKEN'

/**
 * ベースURL
 */
const baseURL: string = ''

/**
 * タイムアウト時間
 */
const timeout: number = 120000

/**
 * axios初期化
 */
const axiosClient = axios.create({
  baseURL: baseURL,
  timeout: timeout,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8',
    'Access-Control-Allow-Origin': '*',
  },
  withCredentials: true,
})

axiosClient.interceptors.request.use(
  (config) => {
    if (config.data !== 'undefined') {
      config.data = JSON.stringify(config.data)
    }

    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**　エラーレスポンス */
const errorRes: ResponseBase = {
  nb_err_cod: '401',
  err_msg: '',
  err_level: '',
}

/**
 * API通信ヘルパ
 */
export class HttpHelper<T extends RequestBase, U extends ResponseBase>
  implements Http<T, U>
{
  /**
   * HTTP Request by get
   * @param url URLの末尾
   * @param param パラメータ
   * @returns apiからの返り値
   */
  async get(url: string, param?: T): Promise<U> {
    // 認証チェック
    const err = this.checkToken(url)
    if (err) {
      return err
    }
    try {
      // 未設定の項目は除外する
      const reqParam = this.createNoEmptyParam(param)
      // GET
      return (await axiosClient.get<U>(url, { params: reqParam })).data
    } catch (error: unknown) {
      return this.catchError(error)
    }
  }

  /**
   * HTTP Request by post
   * @param url URLの末尾
   * @param param パラメータ
   * @returns apiからの返り値
   */
  async post(url: string, param: T): Promise<U> {
    // 認証チェック
    const err = this.checkToken(url)
    if (err) {
      return err
    }
    try {
      // 未設定の項目は除外する
      const reqParam = this.createNoEmptyParam(param)
      // POST
      return (await axiosClient.post<U>(url, reqParam)).data
    } catch (error: unknown) {
      return this.catchError(error)
    }
  }

  /**
   * HTTP Request by put
   * @param url URLの末尾
   * @param param パラメータ
   * @returns apiからの返り値
   */
  async put(url: string, param: T): Promise<U> {
    // 認証チェック
    const err = this.checkToken(url)
    if (err) {
      return err
    }
    try {
      // 未設定の項目は除外する
      const reqParam = this.createNoEmptyParam(param)
      // PUT
      return (await axiosClient.put<U>(url, reqParam)).data
    } catch (error: unknown) {
      return this.catchError(error)
    }
  }

  /**
   * HTTP Request by delete
   * @param url URLの末尾
   * @param param パラメータ
   * @returns apiからの返り値
   */
  async delete(url: string, param: T): Promise<U> {
    // 認証チェック
    const err = this.checkToken(url)
    if (err) {
      return err
    }
    try {
      // 未設定の項目は除外する
      const reqParam = this.createNoEmptyParam(param)
      // DELETE
      return (await axiosClient.delete<U>(url, { params: reqParam })).data
    } catch (error: unknown) {
      return this.catchError(error)
    }
  }

  /**
   * エラー処理
   * @param error エラー内容
   * @returns エラー処理情報
   */
  private catchError(error: unknown): Promise<U> {
    // ※Catch 句の変数型の注釈を指定する場合は、'any' または 'unknown' にする必要があるらしい。

    // タイムアウト→'-1'、エラー→'-2'
    const errorRes: ResponseBase = {
      nb_err_cod: '-2', // エラーの方が多いためこちらで初期化
      err_msg: '',
      err_level: '',
    }
    // AxiosErrorの場合はステータスによって遷移先を変える
    if (axios.isAxiosError(error)) {
      // タイムアウト
      if (this.isTimeout(error.code)) {
        errorRes.nb_err_cod = '-1'
        return Promise.resolve(errorRes).then()
      } //ネットワーク
      if (this.isNetwork(error.code)) {
        errorRes.nb_err_cod = '-3'
        return Promise.resolve(errorRes).then()
      }
      if (error.response) {
        setParams.errCode = error.response.status
      }
    }
    // 上記以外のエラーの場合はエラーページ
    if (setParams.errCode === null || setParams.errCode === '') {
      setParams.errCode = '999'
    }
    router.push({ name: Names.ERROR })
    return Promise.resolve(errorRes).then()
  }

  /**
   * タイムアウトかどうか
   * @param code HttpStatusコード
   * @returns タイムアウトかどうか
   */
  private isTimeout(code: string | undefined): boolean {
    return code === 'ECONNABORTED'
  }

  /**
   * ネットワークエラーかどうか
   * @param code HttpStatusコード
   * @returns ネットワークエラーかどうか
   */
  private isNetwork(code: string | undefined): boolean {
    return code === 'ERR_NETWORK'
  }

  /**
   * 認証トークンをチェック
   * @param url URL
   * @returns エラー
   */
  private checkToken(url: string): Promise<U> | null {
    this.setToken()
    return null
  }

  /**
   * ヘッダに認証トークンを設定
   */
  private setToken(): void {
    axiosClient.defaults.headers[AUTHORIZATION] = 'Bearer ' + getAuth()
    axiosClient.defaults.headers[X_XSRF_TOKEN] = getXsrfToken()
  }

  /**
   * 未設定項目除外パラメータ作成
   * @param パラメータ
   * @returns 未設定項目除外パラメータ
   */
  private createNoEmptyParam(param: T | undefined): any {
    // 未設定の項目は除外する
    const reqParam: any = {}
    for (const paramKey in param) {
      const paramValue = param[paramKey]
      if (paramValue) {
        reqParam[paramKey] = paramValue
      }
    }
    return reqParam
  }
}
