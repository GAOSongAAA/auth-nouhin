import type { InjectionKey, Ref } from 'vue'
// component
import ErrorSnackbar from '../components/view/ErrorSnackbar.vue'
import InfoSnackbar from '../components/view/InfoSnackbar.vue'
// API
import { ApiModel } from '@/api/interface/apiModel'
import { RequestBase, ResponseBase } from '@/types/http'

/**
 * エラースナックバー Injectionキー
 */
export const errorSnackbarKey = Symbol() as InjectionKey<
  Ref<InstanceType<typeof ErrorSnackbar>>
>

/**
 * インフォスナックバー Injectionキー
 */
export const infoSnackbarKey = Symbol() as InjectionKey<
  Ref<InstanceType<typeof InfoSnackbar>>
>

/**
 * 詳細画面初期表示 API Injectionキー
 */
export const getPrescriptionDispKey = Symbol() as InjectionKey<
  ApiModel<RequestBase, ResponseBase>
>

/**
 * 企画名一覧取得 API Injectionキー
 */
export const getProjectNamesKey = Symbol() as InjectionKey<
  ApiModel<RequestBase, ResponseBase>
>

/**
 * DCFコード一覧取得 API Injectionキー
 */
export const getDcfCodeKey = Symbol() as InjectionKey<
  ApiModel<RequestBase, ResponseBase>
>

/**
 * 一覧画面検索 API Injectionキー
 */
export const getPrescriptionListSearchKey = Symbol() as InjectionKey<
  ApiModel<RequestBase, ResponseBase>
>

/**
 * システム設定値取得 API Injectionキー
 */
export const getSystemSettingsKey = Symbol() as InjectionKey<
  ApiModel<RequestBase, ResponseBase>
>
