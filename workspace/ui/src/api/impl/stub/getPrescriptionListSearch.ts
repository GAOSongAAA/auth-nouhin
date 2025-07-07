import { GetRequest, GetResponse } from '@/types/getPrescriptionListSearch'
import { PrescriptionInfo } from '@/types/getPrescriptionListSearch'
import { Http } from '@/api/interface/http'
import { getValue } from '@/util/cookies'
import { wait } from '@/util/promises'

/**
 * 一覧検索 API スタブ
 */
export class GetPrescriptionListSearchStub
  implements Http<GetRequest, GetResponse>
{
  /**
   * 一覧検索
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
      all_su: 0,
      sta_row: 0,
      end_row: 0,
      shm_inf: [],
      shm_dr_input_disabled_flg: 1,
    }

    // cookie値によりレスポンスの場合分け
    const stub = getValue('getPrescriptionListSearch_stub')
    // 4回検索され、5回目は検索されない
    if (!!!stub || stub === '1') {
      const inf: PrescriptionInfo = {
        shm_kkk_cod: '111111',
        shm_kkk_nm: '○○企画',
        shm_egb_nm: '営業1',
        shm_stn_nm: 'A支店',
        shm_ka_nm: 'AA課',
        shm_tts_nm: '佐藤太郎',
        shm_dpy_bno: '222222',
        shm_dpy_kno_gb: '333333',
        shm_dpy_no: '444444',
        shm_dpy_rno: '555555',
        shm_urg_ymd: '2025/02/07',
        shm_hbm_nm: '○○製薬',
        shm_syh_nm: '○○○○薬',
        shm_syh_kkk_yry_nm:
          'ゼポジアカプセル スターターパック7カプセル (49876-123) ',
        shm_nhn_cod: '6666666',
        shm_nhn_nm: '◇◇薬局',
        shm_hnb_nm: '3',
        shm_dcf_cod: '777777777',
        shm_nm: '△△病院',
        shm_dr_nm: '医者太郎次郎',
        shm_mail_adr_mr: 'tokuisaki@gmail.com',
        shm_kkn_ymd: '2025/02/10',
        shm_stat_cod: '2',
        shm_nrk_ymd: '2025/02/08',
        shm_ksn_ymd: '2025/03/10',
    }
    for (let i = 0; i < 50; i++) {
        response.shm_inf.push(inf)
    }
    response.all_su = 200
    response.shm_dr_input_disabled_flg = 0
    }
    // 50件未満の場合、次の検索は行われない=2回目は検索されない
    else if (stub === '2') {
      const inf: PrescriptionInfo = {
        shm_kkk_cod: '111111',
        shm_kkk_nm: '○○企画',
        shm_egb_nm: '営業1',
        shm_stn_nm: 'A支店',
        shm_ka_nm: 'AA課',
        shm_tts_nm: '佐藤太郎',
        shm_dpy_bno: '222222',
        shm_dpy_kno_gb: '333333',
        shm_dpy_no: '444444',
        shm_dpy_rno: '555555',
        shm_urg_ymd: '2025/02/07',
        shm_hbm_nm: '○○製薬',
        shm_syh_nm: '○○○○薬',
        shm_syh_kkk_yry_nm:
          'ゼポジアカプセル スターターパック7カプセル (49876-123) ',
        shm_nhn_cod: '666666',
        shm_nhn_nm: '◇◇薬局',
        shm_hnb_nm: '3',
        shm_dcf_cod: '777777777',
        shm_nm: '△△病院',
        shm_dr_nm: '医者太郎次郎',
        shm_mail_adr_mr: 'tokuisaki@gmail.com',
        shm_kkn_ymd: '2025/02/10',
        shm_stat_cod: '2',
        shm_nrk_ymd: '2025/02/08',
        shm_ksn_ymd: '2025/02/09',
      }
      for (let i = 0; i < 10; i++) {
        response.shm_inf.push(inf)
      }
        response.all_su = 10
        response.shm_dr_input_disabled_flg = 0
    }
    // 2回目は検索されない
    else if (stub === '3') {
      const inf: PrescriptionInfo = {
        shm_kkk_cod: '111111',
        shm_kkk_nm: '○○企画',
        shm_egb_nm: '営業1',
        shm_stn_nm: 'A支店',
        shm_ka_nm: 'AA課',
        shm_tts_nm: '佐藤太郎',
        shm_dpy_bno: '222222',
        shm_dpy_kno_gb: '333333',
        shm_dpy_no: '444444',
        shm_dpy_rno: '555555',
        shm_urg_ymd: '2025/02/07',
        shm_hbm_nm: '○○製薬',
        shm_syh_nm: '○○○○薬',
        shm_syh_kkk_yry_nm:
          'ゼポジアカプセル スターターパック7カプセル (49876-123) ',
        shm_nhn_cod: '666666',
        shm_nhn_nm: '◇◇薬局',
        shm_hnb_nm: '3',
        shm_dcf_cod: '777777777',
        shm_nm: '△△病院',
        shm_dr_nm: '医者太郎次郎',
        shm_mail_adr_mr: 'tokuisaki@gmail.com',
        shm_kkn_ymd: '2025/02/10',
        shm_stat_cod: '2',
        shm_nrk_ymd: '2025/02/08',
        shm_ksn_ymd: '2025/02/09',
      }
      for (let i = 0; i < 50; i++) {
        response.shm_inf.push(inf)
      }
        response.all_su = 50
        response.shm_dr_input_disabled_flg = 0
    }
    // 3回検索され、4回目は検索されない
    else if (stub === '4') {
      const inf: PrescriptionInfo = {
        shm_kkk_cod: '111111',
        shm_kkk_nm: '○○企画',
        shm_egb_nm: '営業1',
        shm_stn_nm: 'A支店',
        shm_ka_nm: 'AA課',
        shm_tts_nm: '佐藤太郎',
        shm_dpy_bno: '222222',
        shm_dpy_kno_gb: '333333',
        shm_dpy_no: '444444',
        shm_dpy_rno: '555555',
        shm_urg_ymd: '2025/02/07',
        shm_hbm_nm: '○○製薬',
        shm_syh_nm: '○○○○薬',
        shm_syh_kkk_yry_nm:
          'ゼポジアカプセル スターターパック7カプセル (49876-123) ',
        shm_nhn_cod: '666666',
        shm_nhn_nm: '◇◇薬局',
        shm_hnb_nm: '3',
        shm_dcf_cod: '777777777',
        shm_nm: '△△病院',
        shm_dr_nm: '医者太郎次郎',
        shm_mail_adr_mr: 'tokuisaki@gmail.com',
        shm_kkn_ymd: '2025/02/10',
        shm_stat_cod: '2',
        shm_nrk_ymd: '2025/02/08',
        shm_ksn_ymd: '2025/02/09',
      }
      const limit = param.offset === 101 ? 1 : 50
      for (let i = 0; i < limit; i++) {
        response.shm_inf.push(inf)
      }
        response.all_su = 101
        response.shm_dr_input_disabled_flg = 2
    }
    // 2回検索され、3回目は検索されない
    else if (stub === '5') {
      const inf: PrescriptionInfo = {
        shm_kkk_cod: '111111',
        shm_kkk_nm: '○○企画',
        shm_egb_nm: '営業1',
        shm_stn_nm: 'A支店',
        shm_ka_nm: 'AA課',
        shm_tts_nm: '佐藤太郎',
        shm_dpy_bno: '222222',
        shm_dpy_kno_gb: '333333',
        shm_dpy_no: '444444',
        shm_dpy_rno: '555555',
        shm_urg_ymd: '2025/02/07',
        shm_hbm_nm: '○○製薬',
        shm_syh_nm: '○○○○薬',
        shm_syh_kkk_yry_nm:
          'ゼポジアカプセル スターターパック7カプセル (49876-123) ',
        shm_nhn_cod: '666666',
        shm_nhn_nm: '◇◇薬局',
        shm_hnb_nm: '3',
        shm_dcf_cod: '777777777',
        shm_nm: '△△病院',
        shm_dr_nm: '医者太郎次郎',
        shm_mail_adr_mr: 'tokuisaki@gmail.com',
        shm_kkn_ymd: '2025/02/10',
        shm_stat_cod: '2',
        shm_nrk_ymd: '2025/02/08',
        shm_ksn_ymd: '2025/02/09',
      }
      const limit = param.offset === 51 ? 1 : 50
      for (let i = 0; i < limit; i++) {
        response.shm_inf.push(inf)
      }
        response.all_su = 51
        response.shm_dr_input_disabled_flg = 2
    }

    // 0件
    else if (stub === '9') {
      response.nb_err_cod = '404'
    }
    //システムエラー
    else if (stub == '10') {
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
