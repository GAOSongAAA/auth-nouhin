import { RequestBase, ResponseBase } from '@/types/http'
import { Http } from '../interface/http'
import { ApiModelBase } from './apiModel'
import { GetPrescriptionListSearchStub } from './stub/getPrescriptionListSearch'

/**
 * 一覧検索 API
 */
export class GetPrescriptionListSearchImpl extends ApiModelBase<
  RequestBase,
  ResponseBase
> {
  /**
   * 一覧検索 API URL
   */
  override getUrl: string = '/mr/prescriptionList_search'

  /**
   * Httpインスタンス（スタブ）作成
   * @returns Httpインスタンス（スタブ）
   */
  override createStub(): Http<RequestBase, ResponseBase> {
    return new GetPrescriptionListSearchStub()
  }
}
