/** 曜日配列 */
const days = ['日', '月', '火', '水', '木', '金', '土']

/**
 * システムタイム
 */
export class SystemTime {
  /**
   * 現在日時取得（形式：YYYYMMDD）
   * @returns 現在日時
   */
  getNowYYYYMMDD(): string {
    const now = new Date()
    return (
      now.getFullYear() +
      ('0' + (now.getMonth() + 1)).slice(-2) +
      ('0' + now.getDate()).slice(-2)
    )
  }
}

/**
 * 現在日時取得（形式：YYYYMMDDHH）
 * @returns 現在日時
 */
export function getNowDatetime(): string {
  const now = new Date()
  return (
    now.getFullYear() +
    ('0' + (now.getMonth() + 1)).slice(-2) +
    ('0' + now.getDate()).slice(-2) +
    ('0' + now.getHours()).slice(-2) +
    ('0' + now.getMinutes()).slice(-2) +
    ('0' + now.getSeconds()).slice(-2)
  )
}

/**
 * 現在日付（形式：MM/DD）
 * @returns 現在日時
 */
export function getNowDate(): string {
  const now = new Date()
  return (
    ('0' + (now.getMonth() + 1)).slice(-2) +
    '/' +
    ('0' + now.getDate()).slice(-2)
  )
}

/**
 * 日付フォーマット変換
 * @param date 年月日情報
 * @returns フォーマット変換済み年月日情報
 */
export function setDateFormat(date: string): string {
  const year: string = date.substring(0, 4)
  const month: string = date.substring(4, 6)
  const day: string = date.substring(6, 8)
  const dataStr: string = year + '/' + month + '/' + day

  return dataStr
}

/**
 * 日時フォーマット変換
 * @param date 日時情報
 * @returns フォーマット変換済み日時情報
 */
export function setDateTimeFormat(date: string): string {
  const array: string[] = date.split(' ')

  const yyMMDD = array[0].split('-')
  const time = array[1].split(':')

  const year: string = yyMMDD[0]
  const month: string = yyMMDD[1]
  const day: string = yyMMDD[2]
  const hour: string = time[0]
  const minutes: string = time[1]
  const seconds: string = time[2].substring(0, 2)
  const dataStr: string =
    year + '/' + month + '/' + day + ' ' + hour + ':' + minutes + ':' + seconds

  return dataStr
}

/**
 * 日時の/を削除してYYYYMMDD形式にして返す。
 * @param date yyyy/MM/dd 形式のデータ
 * @returns 形式変換後のデータ
 */
export function setDateUnformat(date: string): string {
  return date.replaceAll('/', '')
}

/**
 * MM/DD形式にして返す。
 * @param date yyyyMMdd 形式のデータ
 * @returns 形式変換後のデータ
 */
export function setMonthDateformat(date: string): string {
  const month: string = date.substring(4, 6)
  const day: string = date.substring(6, 8)

  return month + '/' + day
}

/**
 * MM月DD日形式にして返す。
 * @param date yyyyMMdd 形式のデータ
 * @returns 形式変換後のデータ
 */
export function setMonthDateJPformat(date: string): string {
  const month: string = date.substring(4, 6)
  const day: string = date.substring(6, 8)

  return month + '月' + day + '日'
}

/**
 * yyyy年MM月DD形式にして返す。
 * @param date yyyyMMdd 形式のデータ
 * @return 形式変換後のデータ
 */
export function setYearMonthDateformat(date: string): string {
  const year: string = date.substring(0, 4)
  const month: string = date.substring(4, 6)
  const day: string = date.substring(6, 8)

  return year + '年' + month + '月' + day + '日'
}

/**
 * hh:mm形式にして返す
 * @param date 日時情報
 * @returns 形式返還後のデータ
 */
export function setHourMinuteFormat(date: string): string {
  const hour: string = date.substring(8, 10)
  const minutes: string = date.substring(10, 12)

  return hour + ':' + minutes
}

/**
 *　指定した日付の曜日を返す
 * @param date yyyyMMdd 形式のデータ
 * @returns 曜日
 */
export function getDate(dateStr: string): string {
  const year: number = Number(dateStr.substring(0, 4))
  const month: number = Number(dateStr.substring(4, 6)) - 1
  const day: number = Number(dateStr.substring(6, 8))
  var date = new Date(year, month, day)
  return '(' + days[date.getDay()] + ')'
}

/**
 * 日時フォーマット変換（秒なし）
 * @param date 日時情報
 * @returns フォーマット変換済み日時情報
 */
export function setDateTimeNotSecondsFormat(date: string): string {
  const year: string = date.substring(0, 4)
  const month: string = date.substring(4, 6)
  const day: string = date.substring(6, 8)
  const hour: string = date.substring(8, 10)
  const minutes: string = date.substring(10, 12)
  const dataStr: string =
    year + '/' + month + '/' + day + ' ' + hour + ':' + minutes

  return dataStr
}

/**
 * 今週の金曜日の日付取得
 * @returns 今週の金曜日の日付情報
 */
export function getFriday(): Date {
  const now = new Date()

  const sunDay = now.getDate() - now.getDay()

  // 今週金曜日の日付取得
  const x = sunDay + 5
  const dayOfWeek = new Date(now.setDate(x))
  // 時刻に19:00を設定する
  dayOfWeek.setHours(19, 0, 0)

  return dayOfWeek
}

/**
 * 有効期限のフォーマットを設定する
 * @param yuk_kgn_param YYYYMMもしくはYYYYMMDD形式
 * @returns YYYY.MMもしくはYYYY.MM.DD YYYYMMもしくはYYYYMMDD形式以外の場合は空文字
 */
export function setYukKgnFormat(yuk_kgn_param: string): string {
  const yuk_kgn = yuk_kgn_param.trim();
  if (yuk_kgn.length === 8) {
    const year: string = yuk_kgn.substring(0, 4)
    const month: string = yuk_kgn.substring(4, 6)
    const day: string = yuk_kgn.substring(6, 8)
    return year + '.' + month + '.' + day
  } else if (yuk_kgn.length === 6) {
    const year: string = yuk_kgn.substring(0, 4)
    const month: string = yuk_kgn.substring(4, 6)
    return year + '.' + month
  }
  return ''
}

/**
 * yyyyMMddhhmm形式にして返す(追加)
 * @param date 日時情報（yyyy/MM/dd(曜日)hh:mm）形式のデータ
 * @returns フォーマット変換済みデータ
 */
export function setYearDateUnformat(date: string): string {
  const year: string = date.substring(0, 4)
  const month: string = date.substring(5, 7)
  const day: string = date.substring(8, 10)
  const hour: string = date.substring(13, 15)
  const minutes: string = date.substring(16, 18)
  const dataStr: string = year + month + day + hour + minutes
  return dataStr
}

/**
 * 時間フォーマット変換（日付なし）(追加)
 * @param date 日時情報（yyyyMMddhhmm）形式のデータ
 * @returns フォーマット変換済み時間
 */
export function setHourMinutFormat(date: string): string {
  const hour: string = date.substring(8, 10)
  const minutes: string = date.substring(10, 12)
  const dataStr: string = hour + ':' + minutes
  return dataStr
}

