import { RequestBase, ResponseBase } from "@/types/http"

/**
 * API Model インタフェース
 */
export interface ApiModel<T extends RequestBase, U extends ResponseBase> {
	/**
	   * GETリクエスト呼び出し
	   * @param param パラメータ
	   */
	get(param?: T): Promise<U>

	/**
	   * POSTリクエスト呼び出し
	   * @param param パラメータ
	   */
	post(param: T): Promise<U>

	/**
	   * PUTリクエスト呼び出し
	   * @param param パラメータ
	   */
	put(param: T): Promise<U>

	/**
	   * DELETEリクエスト呼び出し
	   * @param param パラメータ
	   */
	delete(param: T): Promise<U>
}
