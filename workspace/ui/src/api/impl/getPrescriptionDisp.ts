import { RequestBase, ResponseBase } from '@/types/http'
import { Http } from '../interface/http'
import { ApiModelBase } from './apiModel'
import { GetPrescriptionDispStub } from './stub/getPrescriptionDisp'

/**
 * 詳細画面初期表示 API
 */
export class GetPrescriptionDispImpl extends ApiModelBase<
  RequestBase,
  ResponseBase
> {
  /**
   * 詳細画面初期表示 API URL
   */
  override getUrl: string = '/mr/prescription_disp'

  /**
   * Httpインスタンス（スタブ）作成
   * @returns Httpインスタンス（スタブ）
   */
  override createStub(): Http<RequestBase, ResponseBase> {
    return new GetPrescriptionDispStub()
  }
}
