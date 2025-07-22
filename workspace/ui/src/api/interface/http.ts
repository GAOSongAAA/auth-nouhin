import { RequestBase, ResponseBase } from "@/types/http"

/**
 * API通信 インタフェース
 */
export interface Http<T extends RequestBase, U extends ResponseBase> {
	/**
	   * HTTP Request by get
	   * @param url URLの末尾
	   * @param param パラメータ
	   */
	get(url: string, param?: T): Promise<U>

	/**
	   * HTTP Request by post
	   * @param url URLの末尾
	   * @param param パラメータ
	   */
	post(url: string, param: T): Promise<U>

	/**
	   * HTTP Request by put
	   * @param url URLの末尾
	   * @param param パラメータ
	   */
	put(url: string, param: T): Promise<U>

	/**
	   * HTTP Request by delete
	   * @param url URLの末尾
	   * @param param パラメータ
	   */
	delete(url: string, param: T): Promise<U>
}
