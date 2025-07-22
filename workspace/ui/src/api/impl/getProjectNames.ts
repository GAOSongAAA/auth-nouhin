import { RequestBase, ResponseBase } from '@/types/http'
import { Http } from '../interface/http'
import { ApiModelBase } from './apiModel'
import { GetProjectNamesStub } from './stub/getProjectNames'

/**
 * 企画名リスト取得 API
 */
export class GetProjectNamesImpl extends ApiModelBase<
  RequestBase,
  ResponseBase
> {
  /**
   * 企画名リスト取得 API URL
   */
  override getUrl: string = '/mr/project_name'

  /**
   * Httpインスタンス（スタブ）作成
   * @returns Httpインスタンス（スタブ）
   */
  override createStub(): Http<RequestBase, ResponseBase> {
    return new GetProjectNamesStub()
  }
}
