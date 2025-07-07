/**
 * カレンダーの土日に色を付ける
 * @param date 日付
 * @returns 対応するクラス名
 */
export function getDayClass(date: Date): string {
  // 土曜日を青にする
  const week: number = date.getDay()
  if (week === 6) {
    return 'saturday'
  }
  // 日曜日を赤にする
  if (week === 0) {
    return 'sunday'
  }
  // それ以外
  return ''
}
