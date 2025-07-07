import { GetRequest, GetResponse } from '@/types/getProjectNames'
import { Http } from '@/api/interface/http'
import { getValue } from '@/util/cookies'
import { wait } from '@/util/promises'

/**
 * 企画名リスト取得 API スタブ
 */
export class GetProjectNamesStub implements Http<GetRequest, GetResponse> {
  /**
   * 企画名リスト取得
   * @param url URLの末尾
   * @param param パラメータ
   * @returns APIレスポンス
   */
  async get(url: string, param: GetRequest): Promise<GetResponse> {
    //1000ミリ秒待つ
    await wait(1000)

    // レスポンス定義
    const response: GetResponse = {
      nb_err_cod: '200',
      err_msg: '',
      err_level: '',
      lst_kkk_inf: [],
    }

    // cookie値によりレスポンスの場合分け
    const stub = getValue('getProjectNames_stub')
    if (!!!stub || stub === '1') {
      response.lst_kkk_inf = [
        {
          kkk_cod: '111111',
          kkk_nm: '○○企画',
        },
        {
          kkk_cod: '222222',
          kkk_nm: '◇◇企画',
        },
        {
          kkk_cod: '333333',
          kkk_nm: '△△企画企画企画企画企画企画',
        },
      ]
    }
    // 0件
    else if (stub === '4') {
      response.nb_err_cod = '404'
    }
    //システムエラー
    else if (stub == '5') {
      response.nb_err_cod = '500'
    }
    // タイムアウト
    else if (stub === '11') {
      response.nb_err_cod = '-1'
    }
    // ネットワークエラー
    else if (stub === '12') {
      response.nb_err_cod = '-3'
    }
    return response
  }

  async post(url: string, param: GetRequest): Promise<GetResponse> {
    throw new Error('Method not implemented.')
  }

  put(url: string, param: GetRequest): Promise<GetResponse> {
    throw new Error('Method not implemented.')
  }

  delete(url: string, param: GetRequest): Promise<GetResponse> {
    throw new Error('Method not implemented.')
  }
}
