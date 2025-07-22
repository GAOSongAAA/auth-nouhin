import { reactive } from "vue";

/**
 * エラーコード情報（システムエラー画面にて表示）
 */
export const setParams: params = reactive({
  errCode: "",
});

/**
* エラーコード情報（システムエラー画面にて表示）
* @property  errCode エラーコード
*/
export type params = {
  errCode: string | unknown;
};