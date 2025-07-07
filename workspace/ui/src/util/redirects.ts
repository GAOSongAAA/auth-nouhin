import router from '@/router'
import { setParams as setParamsErr } from '@/util/errCodeParam'
import * as Names from '@/router/names'

/**
 * システムエラー画面へリダイレクトする
 * @param errcd エラーコード
 */
export function redirectError(errcd: string): void {
  setParamsErr.errCode = errcd
  router.push({ name: Names.ERROR })
}
