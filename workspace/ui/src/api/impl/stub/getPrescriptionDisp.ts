import { GetRequest, GetResponse } from '@/types/getPrescriptionDisp'
import { Http } from '@/api/interface/http'
import { getValue } from '@/util/cookies'
import { wait } from '@/util/promises'

/**
 * 詳細画面初期表示 API スタブ
 */
export class GetPrescriptionDispStub
  implements Http<GetRequest, GetResponse>
{
  /**
   * 詳細画面初期表示
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
      pre_kkk_nm: '',
      pre_nhn_nm: '',
      pre_tok_cod: '',
      pre_dnp_ymd: '',
      pre_dnp_no: '',
      pre_syh_kkk_yry: '',
      pre_syh_cod: '',
      pre_hnb_num: '',
      pre_dcf_cod: '',
      pre_nm: '',
      pre_ful_nm: '',
      pre_cod: 0,
      pre_ryu: '',
      pre_dr: '',
      pre_dr_cod: 0,
      pre_dr_input_flg: 1,
      pre_dp: '',
      pre_dp_cod: 0,
      pre_mr_ren: '',
      pre_stat_flg: 0,
    }

    // cookie値によりレスポンスの場合分け
    const stub = getValue('getPrescriptionDisp_stub')
    if (stub === '1') {
      response.pre_kkk_nm = '○○企画'
      response.pre_nhn_nm = 'A薬局'
      response.pre_tok_cod = '0000-00'
      response.pre_dnp_ymd = '2025/04/03'
      response.pre_dnp_no = 'XXXXXXX-XXX-XXX'
      response.pre_syh_kkk_yry = 'ゼポジアカプセル スターターパック7カプセル'
      response.pre_syh_cod = '49876-123'
      response.pre_hnb_num = '3'
      response.pre_dcf_cod = ''
      response.pre_nm = ''
      response.pre_ful_nm = ''
      response.pre_cod = 1
      response.pre_ryu = ''
      response.pre_dr = '',
      response.pre_dr_cod = 0,
      response.pre_dr_input_flg = 1
      response.pre_dp = ''
      response.pre_dp_cod = 1
      response.pre_mr_ren = ''
      response.pre_stat_flg = 0
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
    } else {
      response.pre_kkk_nm = '○○企画'
      response.pre_nhn_nm = 'A薬局'
      response.pre_tok_cod = '0000000'
      response.pre_dnp_ymd = '2025/04/03'
      response.pre_dnp_no = 'XXXXXXX-XXX-XXX'
      response.pre_syh_kkk_yry = 'ゼポジアカプセル スターターパック7カプセル'
      response.pre_syh_cod = '49876-123'
      response.pre_hnb_num = '3'
      response.pre_dcf_cod = '123456789'
      response.pre_nm = 'ＡＡＡ病院'
      response.pre_ful_nm = '正式'
      response.pre_cod = 1
      response.pre_ryu = ''
      response.pre_dr = 'ＡＡ先生'  
      response.pre_dr_cod = 0,
      response.pre_dr_input_flg = 0
      response.pre_dp = 'ＡＡ診療科'
      response.pre_dp_cod = 1
      response.pre_mr_ren = '連絡事項です'
      response.pre_stat_flg = 0
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
