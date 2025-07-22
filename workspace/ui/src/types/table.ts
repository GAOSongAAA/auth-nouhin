/**
 * テーブルヘッダ 型定義
 * ※Vuetify内部の型定義はexportされていなかった
 * @property key キー
 * @property title タイトル
 * @property sortable ソート情報
 * @property tdAlign　表示位置
 * @property width　横幅
 */
export type Headers = {
  class: string,
  key: string,
  title: string,
  sortable: boolean,
  tdAlign: 'left' | 'center' | 'right' | 'justify' | 'char',
  width: string
}

/**
 * テーブルアイテムベース 型定義
 */
export type ItemsBase = {
  
}

/**
 * 履歴 一覧 型定義
 * @property hch_trk_dt - 登録日時(発注)日付部分
 * @property hch_trk_min - 登録日時(発注)時間部分
 * @property hch_kt_dt - 発注確定日時日付部分
 * @property hch_kt_min - 発注確定日時時間部分
 * @property hch_user_nm - ユーザー名(発注)
 * @property hen_trk_dt - 登録日時(返品)日付部分
 * @property hen_trk_min - 登録日時(返品)時間部分
 * @property hen_kt_dt - 返品確定日時日付部分
 * @property hen_kt_min - 返品確定日時時間部分
 * @property hen_usr_nm - ユーザー名(返品)
 * @property tn_dt_stk_week - 提案データ取得週
 * 
 */
export type HistoryItem = {
  hch_trk_dt: string
    hch_trk_min: string
    hch_kt_dt: string
    hch_kt_min: string
    hch_usr_nm: string
    hen_trk_dt: string
    hen_trk_min: string
    hen_kt_dt: string
    hen_kt_min: string
    hen_usr_nm: string
    tn_dt_stk_week: string
} & ItemsBase

