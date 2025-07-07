import { GetRequest, GetResponse } from '@/types/getDcfCode'
import { Http } from '@/api/interface/http'
import { getValue } from '@/util/cookies'
import { wait } from '@/util/promises'

/**
 * DCFコードリスト取得 API スタブ
 */
export class GetDcfCodeStub implements Http<GetRequest, GetResponse> {
  /**
   * DCFコードリスト取得
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
      dcf_cod: [],
    }

    // cookie値によりレスポンスの場合分け
    const stub = getValue('getDcfCode_stub')
    if (!!!stub || stub === '1') {
        response.dcf_cod = [
         '1111111',
         '2222222',
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
