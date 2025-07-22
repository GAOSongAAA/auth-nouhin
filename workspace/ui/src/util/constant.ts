/** MRAPIURL */
export const mrUserUrl: string = '/mr'

/** 本部APIURL */
export const hnbUserUrl: string = '/honbu'

/** MoveURL_エラー */
export const moveURL_Error: string = '/#/error'

/** 本部フラグ設定値（判定用） */
export const judge_honbu_flag: number = 1

/** 一度の検索で取得する最大件数 */
export const searchLimit: number = 50

/** 状態 コードと名称の対応 */
const statList = {
  '0': '0',
  '1': '1',
  '2': '2',
  '3': '3',
  '4': '4',
} as const

/** 状態 コードと名称の対応 */
export type statList = (typeof statList)[keyof typeof statList]

export const getStatName = (statCode: statList) => {
  switch (statCode) {
    case statList['0']:
      return '未'
    case statList['1']:
      return '済'
    case statList['2']:
      return '済（不明あり）'
    case statList['3']:
      return '再'
    case statList['4']:
      return '再（不明あり）'
    default:
      return ''
  }
}
