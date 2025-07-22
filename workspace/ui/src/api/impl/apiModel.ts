import { ErrorCode, RequestBase, ResponseBase } from '@/types/http'
import { ApiModel } from '../interface/apiModel'
import { Http } from '../interface/http'
import { HttpHelper } from './httpHelper'
import { Mode } from '../../assets/config.json'
import { redirectError } from '@/util/redirects'
import { autoUpdateCookies } from '@/util/cookies'
import { getMoveURL } from '@/util/cookies'
import { deleteCookiesWhenLogout, getLogoutURL } from '@/util/cookies'
import { Ref, ref } from 'vue'
/**
 * API Model ベース
 */
export class ApiModelBase<T extends RequestBase, U extends ResponseBase>
  implements ApiModel<T, U>
{
  /**
   * リクエスト用 エンドポイント 接頭辞
   */
  prefixUrl: () => string = () => {
    switch (getMoveURL()) {
      default:
        return ''
    }
  }

  /**
   * GET リクエスト用 エンドポイント
   */
  getUrl: string = ''

  /**
   * POST リクエスト用 エンドポイント
   */
  postUrl: string = ''

  /**
   * PUT リクエスト用 エンドポイント
   */
  putUrl: string = ''

  /**
   * DELETE リクエスト用 エンドポイント
   */
  deleteUrl: string = ''

  /**
   * API通信ヘルパ
   */
  helper: Http<T, U>

  /** ログアウトURL */
  logoutUrl: Ref<string> = ref<string>('')

  /**
   * コンストラクタ
   */
  constructor() {
    this.helper = this.create()
  }

  /**
   * GETリクエスト呼び出し
   * @param param パラメータ
   * @returns APIレスポンス
   */
  async get(param?: T): Promise<U> {
    // GET
    const res = await this.helper.get(this.getUrl, param)
    // cookie有効期限更新
    autoUpdateCookies()
    // エラー処理
    this.judgeErrorCode(res)
    return res
  }

  /**
   * POSTリクエスト呼び出し
   * @param param パラメータ
   * @returns APIレスポンス
   */
  async post(param: T): Promise<U> {
    // POST
    const res = await this.helper.post(this.postUrl, param)
    // cookie有効期限更新
    autoUpdateCookies()
    // エラー処理
    this.judgeErrorCode(res)
    return res
  }

  /**
   * PUTリクエスト呼び出し
   * @param param パラメータ
   * @returns APIレスポンス
   */
  async put(param: T): Promise<U> {
    // PUT
    const res = await this.helper.put(this.putUrl, param)
    // cookie有効期限更新
    autoUpdateCookies()
    // エラー処理
    this.judgeErrorCode(res)
    return res
  }

  /**
   * DELETEリクエスト呼び出し
   * @param param パラメータ
   * @returns APIレスポンス
   */
  async delete(param: T): Promise<U> {
    // DELETE
    const res = await this.helper.delete(this.deleteUrl, param)
    // cookie有効期限更新
    autoUpdateCookies()
    // エラー処理
    this.judgeErrorCode(res)
    return res
  }

  /**
   * エラー判定
   * @param res APIレスポンス
   */
  protected judgeErrorCode<U extends ResponseBase>(res: U): void {
    // エラーページに遷移
    if (this.isGoErrorPage(res.nb_err_cod)) {
      redirectError(res.nb_err_cod)
    } else if (this.isGoRedirectPage(res.nb_err_cod)) {
      // CookieからログアウトURLを取得する
      this.logoutUrl.value = getLogoutURL()
      // Cookieを削除する
      deleteCookiesWhenLogout()
      // ログアウトURLへ遷移
      window.location.href = this.logoutUrl.value
    }
  }

  /**
   * Httpインスタンス（スタブ）作成
   * @returns Httpインスタンス（スタブ）
   */
  protected createStub(): Http<T, U> {
    // スタブは継承先で定義する
    return new HttpHelper()
  }

  /**
   * Httpインスタンス作成
   * @returns Httpインスタンス
   */
  private create(): Http<T, U> {
    // if (Mode === 'development') {
    //   return this.createStub()
    // } else {
    //   return new HttpHelper()
    // }
    return new HttpHelper()
  }

  /**
   * エラーページに遷移するかどうかを内部エラーコードから判定
   * @param code 内部エラーコード
   * @returns エラーページに遷移するかどうか
   */
  private isGoErrorPage(code: ErrorCode): boolean {
    return code === '400' || code === '403' || code === '500'
  }

  /**
   * ログインページに遷移するかどうかを内部エラーコードから判定
   * @param code 内部エラーコード
   * @returns ログインページに遷移するかどうか
   */
  private isGoRedirectPage(code: ErrorCode): boolean {
    return code === '302' || code === '401'
  }
}
