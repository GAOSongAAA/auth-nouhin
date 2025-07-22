import { RequestBase, ResponseBase } from '@/types/http'
import { Http } from '../interface/http'
import { ApiModelBase } from './apiModel'
import { GetDcfCodeStub } from './stub/getDcfCode'

/**
 * DCFコードリスト取得 API
 */
export class GetDcfCodeImpl extends ApiModelBase<
  RequestBase,
  ResponseBase
> {
  /**
   * DCFコードリスト取得 API URL
   */
  override getUrl: string = '/mr/dcf_code'

  /**
   * Httpインスタンス（スタブ）作成
   * @returns Httpインスタンス（スタブ）
   */
  override createStub(): Http<RequestBase, ResponseBase> {
    return new GetDcfCodeStub()
  }
}
