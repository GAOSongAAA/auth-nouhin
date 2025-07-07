import { RequestBase, ResponseBase } from "@/types/http";
import { ApiModelBase } from "./apiModel";
import { Http } from "../interface/http";
import { GetSystemSettingsStub } from "./stub/getSystemSettingsStub";

/**
 * システム設定値取得 API
 */
export class GetSystemSettingsImpl extends ApiModelBase<
  RequestBase,
  ResponseBase
> {
    /**
     * システム設定値取得 API URL
     */
    override getUrl: string = '/system_setting'

    /**
     * Httpインスタンス（スタブ）作成
     * @returns Httpインスタンス（スタブ）
     */
    protected override createStub(): Http<RequestBase, ResponseBase> {
        return new GetSystemSettingsStub()
    }
}