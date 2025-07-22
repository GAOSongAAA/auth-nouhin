<template>
  <!-- ヘッダ部分 -->
  <Header :menu-view-fg="true" />
  <div class="customer_search_content">
    <!-- 検索条件入力欄 -->
    <div class="customer_search_menu">
      <v-container fluid class="ma-0 px-0 pt-2 pb-2">
        <v-row class="mx-0">
          <v-col :cols="mobile ? 6 : '3'" class="pt-1 pb-1">
            <c-select
              title="企画名"
              :items="kkk_options"
              v-model:selectedItem="kkk_options_def"
              @update:model-value="getDcfCode"
            />
          </v-col>
            <v-col :cols="mobile ? 6 : '3'" class="pt-1 pb-1" v-if="getHonbuFlag() === '0'">
              <c-select
                title="DCFコード"
                :items="dcf_options"
                v-model:selectedItem="dcf_options_def"
                @update:model-value="getProjectNames"
            />
          </v-col>
            <v-col :cols="mobile ? 13 : 3" class="pt-1 pb-1" v-if="getHonbuFlag() === '1'">
            <c-text-area
              placeholder="DCFコード"
              maxlength="9"
              v-model:input="dcf_options_def"
            />
          </v-col>
        </v-row>
        <v-row class="mx-0">
          <v-col :cols="mobile ? 5 : 2" class="pt-1">
            <CVueDatePicker
              v-model:date="urg_ymd_stt"
              placeholder="売上日 開始日"
            >
            </CVueDatePicker>
          </v-col>
          <v-col cols="1" class="pt-1">
            <div
              class="test_text d-flex align-center justify-center fill-height"
            >
              ～
            </div>
          </v-col>
          <v-col :cols="mobile ? 5 : 2" class="pt-1">
            <CVueDatePicker
              v-model:date="urg_ymd_end"
              placeholder="売上日 終了日"
            >
            </CVueDatePicker>
          </v-col>
          <v-spacer />
          <v-col
            v-if="!mobile && searchResultList.length > 0"
            cols="auto"
            class="d-flex align-center pt-1 pb-1"
          >
            件数：{{ all_su }}件
          </v-col>
          <v-col v-if="!mobile" cols="auto" class="pt-2 pb-0">
            <c-btn
              class="text-black nouki_btn_outlined"
              text="クリア"
              prependIcon=""
              variant="outlined"
              @click="clearSearchCriteria()"
          /></v-col>
          <v-col v-if="!mobile" cols="auto" class="pt-2 pb-0">
            <c-btn
              type="submit"
              text="検索"
              class="customer_search_company_btn nouki_btn"
              @click="clearSearchResult(), triggerLoad()"
          /></v-col>
        </v-row>
        <!-- モバイル表示用の行 -->
        <v-row v-if="mobile" class="mx-0 d-flex justify-end">
          <v-col
            v-if="searchResultList.length > 0"
            cols="auto"
            class="d-flex align-center pt-0"
          >
            件数：{{ all_su }}件
          </v-col>
          <v-col cols="auto" class="pt-0">
            <c-btn
              class="text-black nouki_btn_outlined"
              text="クリア"
              prependIcon=""
              variant="outlined"
              @click="clearSearchCriteria()"
          /></v-col>
          <v-col cols="auto" class="pt-0">
            <c-btn
              type="submit"
              text="検索"
              class="customer_search_company_btn nouki_btn"
              @click="clearSearchResult(), triggerLoad()"
          /></v-col>
        </v-row>
      </v-container>
    </div>
  </div>
  <!-- 検索結果 -->
  <div class="customer_search_inner">
    <!-- 検索結果テーブル -->
    <v-infinite-scroll
      ref="infScroll"
      :mode="
        searchCount >= 1 && searchResultList.length < all_su
          ? 'intersect'
          : 'manual'
      "
      class="infinite-scroll-container"
      @load="tableLoad"
    >
      <v-data-table-virtual
        class="pre-line"
        density="compact"
        :headers="headers"
        :items="searchResultList"
        item-value="DCFName"
        no-data-text=""
        hover
        >
        <template #header.shm_dr_nm="{ column }">
          {{ shm_dr_all_input_disabled_flg  ? '備考' : '処方医' }}
        </template>
        <template #[`item.shm_kkk_nm`]="{ item }">
          <span
            class="customer_code_item_a"
            @click="
              toPrescriptionDetail(
                item.shm_dpy_bno,
                item.shm_dpy_kno_gb,
                item.shm_dpy_no,
                item.shm_dpy_rno,
                item.shm_urg_ymd.replace(/\//g, '')
              )
            "
            >{{ item.shm_kkk_nm }}</span
          >
        </template>
      </v-data-table-virtual>
      <template #load-more></template>
      <template #empty></template>
    </v-infinite-scroll>
  </div>
</template>

<script setup lang="ts">
import {
  Ref,
  ref,
  onMounted,
  computed,
  inject,
  useTemplateRef,
  ComponentPublicInstance,
} from 'vue'
import { useRouter } from 'vue-router'
import Header from '@/components/view/Header.vue'
import CBtn from '@/components/control/CBtn.vue'
import CSelect from '@/components/control/CSelect.vue'
import CTextArea from '@/components/control/CTextArea.vue'
import CVueDatePicker from '@/components/control/CVueDatePicker.vue'
import { SelectsBase } from '@/types/selects'
import { Headers } from '@/types/table'
import {
  errorSnackbarKey,
  getProjectNamesKey,
  getDcfCodeKey,
  getPrescriptionListSearchKey,
} from '@/util/keys'
import { setRequestParameterR, getHonbuFlag } from '@/util/cookies'

import {
  GetRequest as GetProjectNamesRequest,
  GetResponse as GetProjectNamesResponse,
} from '@/types/getProjectNames'

import {
  GetRequest as GetDcfCodeRequest,
  GetResponse as GetDcfCodeResponse,
} from '@/types/getDcfCode'

import {
  PrescriptionInfo,
  GetRequest as GetPrescriptionListSearchRequest,
  GetResponse as GetPrescriptionListSearchResponse,
} from '@/types/getPrescriptionListSearch'

import { GetProjectNamesImpl } from '@/api/impl/getProjectNames'
import { GetDcfCodeImpl} from '@/api/impl/getDcfCode'
import { GetPrescriptionListSearchImpl } from '@/api/impl/getPrescriptionListSearch'
import {
  searchLimit,
  getStatName,
  statList,
} from '@/util/constant'
import { useDisplay } from 'vuetify'
import messageList from '@/util/message.json'
import { PrescriptionDetail } from '@/router/names'

const { mobile } = useDisplay()

// API
/** 企画名リスト取得 */
const getProjectNamesApi = inject(getProjectNamesKey) as GetProjectNamesImpl

/** DCFコードリスト取得 */
const getDcfCodeApi = inject(getDcfCodeKey) as GetDcfCodeImpl

/** 一覧画面検索 */
const getPrescriptionListSearchApi = inject(
  getPrescriptionListSearchKey
) as GetPrescriptionListSearchImpl

/** 処方医不明有無フラグ */
const shm_dr_all_input_disabled_flg = ref<boolean>(true)

/** スナックバー定義 */
const snackbar = inject(errorSnackbarKey)

/** ルーター */
const router = useRouter()

/** 無限スクロール */
const infScrollRef = useTemplateRef<ComponentPublicInstance>('infScroll')

/** リスト選択肢 選択なし */
const emptySelect: SelectsBase = {
  title: '',
  value: '',
}

/** 企画名リスト */
const kkk_options: Ref<Array<SelectsBase>> = ref<Array<SelectsBase>>([
  emptySelect,
])

/** DCFコードリスト */
const dcf_options: Ref<Array<SelectsBase>> = ref<Array<SelectsBase>>([
    emptySelect,
])

/** 検索条件：企画名（コード） */
const kkk_options_def: Ref<string> = ref<string>('')
/** 検索条件：DCFコード（コード） */
const dcf_options_def: Ref<string> = ref<string>('')
/** 検索条件：売上日 開始日 */
const urg_ymd_stt: Ref<string> = ref<string>('')
/** 検索条件：売上日 終了日 */
const urg_ymd_end: Ref<string> = ref<string>('')

/** 検索処理中かどうか管理するフラグ */
const isLoading = ref(false)

/** 無限スクロールの状態
 * ok：次を読み込む
 * empty：これ以上読み込まない
 * error：エラー
 */
const infiniteScrollEvents: Ref<
  ((value: 'ok' | 'empty' | 'error') => void) | undefined
> = ref()

/** 総件数 */
const all_su = ref<number>(0)
/** 終了行 */
const end_row = ref<number>(0)

/** 一覧表示情報（API）　*/
const searchResultList: Ref<Array<PrescriptionDisplayInfo>> = ref<
  Array<PrescriptionDisplayInfo>
>([])

/** 一覧表示情報（画面）　*/
type PrescriptionDisplayInfo = {
  /** 所属（営業部+支店+課） */
  szk_nm: string
  /** 状態（名称） */
  stat_nm: string
  /** 納品先名 (納品先コード）*/
  shm_nhn_nm_disp: string
} & PrescriptionInfo

/** 検索回数 */
const searchCount: Ref<number> = ref(0)

/** ヘッダー（モバイル表示） */
const mobileHeaders: Array<Headers> = [
  {
    key: 'shm_kkk_nm',
    title: '企画名',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '10%',
  },
  {
    key: 'shm_syh_kkk_yry_nm',
    title: '品名・規格・容量',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '25%',
  },
  {
    key: 'shm_hnb_nm',
    title: '数量',
    sortable: false,
    tdAlign: 'right',
    class: '',
    width: '5%',
  },
]

/** ヘッダー（PC表示）（MR） */
const pcHeadersMr: Array<Headers> = [
  {
    key: 'shm_kkk_nm',
    title: '企画名',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'szk_nm',
    title: '所属',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_tts_nm',
    title: '得意先担当者',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_urg_ymd',
    title: '売上日',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'shm_hbm_nm',
    title: 'メーカー',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_syh_kkk_yry_nm',
    title: '品名・規格・容量',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_nhn_nm_disp',
    title: '納品先名',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '8%',
  },
  {
    key: 'shm_hnb_nm',
    title: '数量',
    sortable: false,
    tdAlign: 'right',
    class: '',
    width: '1%',
  },
  {
      key: 'shm_nm',
      title: '処方元',
      sortable: false,
      tdAlign: 'left',
      class: '',
      width: '8%',
  },
  {
      key: 'shm_dcf_cod',
      title: 'DCFコード',
      sortable: false,
      tdAlign: 'left',
      class: '',
      width: '4%',
  },
  {
    key: 'shm_dr_nm',
    title: '備考',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'shm_nrk_ymd',
    title: '入力日時',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
]

/** ヘッダー（PC表示）（本部） */
const pcHeadersHonbu: Array<Headers> = [
  {
    key: 'shm_kkk_nm',
    title: '企画名',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'szk_nm',
    title: '所属',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_tts_nm',
    title: '得意先担当者',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_urg_ymd',
    title: '売上日',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'shm_hbm_nm',
    title: 'メーカー',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_syh_kkk_yry_nm',
    title: '品名・規格・容量',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '8%',
  },
  {
    key: 'shm_nhn_nm_disp',
    title: '納品先名',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '8%',
  },
  {
    key: 'shm_hnb_nm',
    title: '数量',
    sortable: false,
    tdAlign: 'right',
    class: '',
    width: '1%',
  },
  {
      key: 'shm_nm',
      title: '処方元',
      sortable: false,
      tdAlign: 'left',
      class: '',
      width: '8%',
　},
  {
      key: 'shm_dcf_cod',
      title: 'DCFコード',
      sortable: false,
      tdAlign: 'left',
      class: '',
      width: '4%',
  },
  {
    key: 'shm_dr_nm',
    title: '備考',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'shm_mail_adr_mr',
    title: 'MR担当者ID',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '6%',
  },
  {
    key: 'shm_kkn_ymd',
    title: '期限日',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'stat_nm',
    title: '状態',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
  {
    key: 'shm_nrk_ymd',
    title: '入力日時',
    sortable: false,
    tdAlign: 'left',
    class: '',
    width: '4%',
  },
]

/** ヘッダー */
const headers = computed(() => {
    // 本部フラグが1の場合（本部）
    if (getHonbuFlag() === '1') {
        return mobile.value ? mobileHeaders : pcHeadersHonbu
    } else {
    // 本部フラグが0の場合（MR）
        return mobile.value ? mobileHeaders : pcHeadersMr
    } 
})

/**
 * 一覧画面検索
 */
const prescriptionListSearch: () => Promise<void> = async () => {
  // 処理が実行中の場合
  if (isLoading.value) return
  isLoading.value = true

  const request: GetPrescriptionListSearchRequest = {
    limit: searchLimit,
    offset: searchCount.value * searchLimit + 1,
    kkk_cod: kkk_options_def.value,
    dcf_cod: dcf_options_def.value,
    urg_ymd_stt: urg_ymd_stt.value,
    urg_ymd_end: urg_ymd_end.value,
  }

  const res: GetPrescriptionListSearchResponse =
    (await getPrescriptionListSearchApi.get(
      request
    )) as GetPrescriptionListSearchResponse

  // 処理が終了した場合
  isLoading.value = false

  if (res.nb_err_cod !== '200') {
    // ネットワーク関連エラー
    snackbar!.value.showIfNetworkError(res.nb_err_cod)
    if (res.nb_err_cod === '404') {
      // 検索結果が取得できない場合、スナックバーを表示する
      snackbar!.value.show(messageList.E000002)
      throw new Error()
    }
     return
  }

  // 取得データを設定する
  const searchResultDisplayList: PrescriptionDisplayInfo[] = res.shm_inf.map(
    (info) => {
      return info as PrescriptionDisplayInfo
    }
  )
  searchResultDisplayList.forEach((info) => {
    // 所属は営業部+支店+課
    info.szk_nm = `${info.shm_egb_nm}/${info.shm_stn_nm}/${info.shm_ka_nm}`
    // 状態コードから状態名を取得
    info.stat_nm = getStatName(info.shm_stat_cod as statList)
    // 納品先名は納品先（納品先コード）
    info.shm_nhn_nm_disp = `${info.shm_nhn_nm}\n（${info.shm_nhn_cod.slice(
      0,
      4
    )}-${info.shm_nhn_cod.slice(4)}）`
  })
  searchResultList.value = searchResultList.value.concat(
    searchResultDisplayList
  )
  searchCount.value++
  all_su.value = res.all_su

  // オフセットが1の場合、処方医入力有無フラグを設定
  if (request.offset === 1 ) {
    shm_dr_all_input_disabled_flg.value = res.shm_dr_input_disabled_flg === 1
    } 
   
  // 現在の取得件数が総件数よりも少ない場合は、次回の検索を行うようにする
  // そうでない場合は、これ以上検索しない
  if (all_su.value <= searchResultList.value.length) {
    // これ以上検索しない
    throw new Error()
  }
}

const triggerLoad: () => Promise<void> = async () => {
  if (infScrollRef.value) {
    infScrollRef.value.$emit('load', { side: 'end', done() {} })
  }
}

/** スクロールが一番下まで達した際のイベント */
const tableLoad: ({
  done,
}: {
  done: (status: 'error' | 'loading' | 'empty' | 'ok') => void
}) => Promise<void> = async ({
  done,
}: {
  done: (status: 'error' | 'loading' | 'empty' | 'ok') => void
}) => {
  infiniteScrollEvents.value = done
  try {
    await prescriptionListSearch()
    done('ok')
  } catch (e) {
    done('empty')
  }
}

/** 詳細画面遷移 */
const toPrescriptionDetail: (
  shm_dpy_bno: string,
  shm_dpy_kno_gb: string,
  shm_dpy_no: string,
  shm_dpy_rno: string,
  shm_urg_ymd: string
) => void = (
  shm_dpy_bno: string,
  shm_dpy_kno_gb: string,
  shm_dpy_no: string,
  shm_dpy_rno: string,
  shm_urg_ymd: string
) => {
  // cookieのrequestParameterを更新
  updateRequestParameter(
    shm_dpy_bno,
    shm_dpy_kno_gb,
    shm_dpy_no,
    shm_dpy_rno,
    shm_urg_ymd
  )
  router.push({ name: PrescriptionDetail })
}

/** cookieのrequestParameterを更新 */
const updateRequestParameter: (
  shm_dpy_bno: string,
  shm_dpy_kno_gb: string,
  shm_dpy_no: string,
  shm_dpy_rno: string,
  shm_urg_ymd: string
) => void = (
  shm_dpy_bno: string,
  shm_dpy_kno_gb: string,
  shm_dpy_no: string,
  shm_dpy_rno: string,
  shm_urg_ymd: string
) => {
  const rpJson = `{"DPY_BNO": "${shm_dpy_bno}","DPY_KNO": "${shm_dpy_kno_gb}","DPY_NO": "${shm_dpy_no}","DPY_LNO": "${shm_dpy_rno}","URIAGE_YMD": "${shm_urg_ymd}"}`

  // UTF-8バイト配列に変換
  const utf8Bytes = new TextEncoder().encode(rpJson)

  // Base64エンコード
  const base64String = btoa(String.fromCharCode(...utf8Bytes))

  // Base64URL形式に変換
  const base64URLString = base64String
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '')

  setRequestParameterR(base64URLString)
}

/** 企画名リスト取得 */
const getProjectNames: () => Promise<void> = async () => {
  const param: GetProjectNamesRequest = {
    dcf_cod: dcf_options_def.value,
  }

  // 企画名リスト取得APIを呼び出す
  const res: GetProjectNamesResponse = (await getProjectNamesApi.get(
    param
  )) as GetProjectNamesResponse

  // エラーの場合
  if (res.nb_err_cod !== '200') {
    // ネットワーク関連エラー
    snackbar!.value.showIfNetworkError(res.nb_err_cod)
    if (res.nb_err_cod === '404') {
      // 取得できない場合、スナックバーを表示する
      snackbar!.value.show(messageList.E000013)

      // リストと選択肢を初期化する
      kkk_options.value = [emptySelect]
      kkk_options_def.value = ''
    }
    return
  }

  kkk_options.value = [emptySelect].concat(
    res.lst_kkk_inf.map((x) => {
      return { title: x.kkk_nm, value: x.kkk_cod }
    })
  )
}

/** DCFコードリスト取得 */
const getDcfCode: () => Promise<void> = async () => {
  //本部フラグが0（MR）の場合に呼び出す
  if (getHonbuFlag() === '0') {
  const param: GetDcfCodeRequest = {
    kkk_cod: kkk_options_def.value
  }

  // DCFコードリスト取得APIを呼び出す
  const res: GetDcfCodeResponse = (await getDcfCodeApi.get(
    param
  )) as GetDcfCodeResponse

  // エラーの場合
  if (res.nb_err_cod !== '200') {
    // ネットワーク関連エラー
    snackbar!.value.showIfNetworkError(res.nb_err_cod)
    if (res.nb_err_cod === '404') {
      // 取得できない場合、スナックバーを表示する
      snackbar!.value.show(messageList.E000014)

      // リストと選択肢を初期化する
      dcf_options.value = [emptySelect]
      dcf_options_def.value = ''
    }
    return
  }

  dcf_options.value = [emptySelect].concat(
    res.dcf_cod.map((x) => {
      return { title: x, value: x }
      })
    )
  }
}

    /** 検索条件削除 */
    const clearSearchCriteria: () => Promise<void> = async () => {
        clearKkk()
        clearDcf()

        urg_ymd_stt.value = ''
        urg_ymd_end.value = ''

        await getProjectNames()
        await getDcfCode()
    }

    /** 検索条件：企画名をクリア */
    const clearKkk: () => void = () => {
        kkk_options.value = [emptySelect]
        kkk_options_def.value = ''
    }

    /** 検索条件：DCFコードをクリア */
    const clearDcf: () => void = () => {
        dcf_options.value = [emptySelect]
        dcf_options_def.value = ''
    }

    /** 検索結果初期化 */
    const clearSearchResult: () => void = () => {
  if (infiniteScrollEvents.value) {
    infiniteScrollEvents.value('ok')
  }
  searchCount.value = 0
  searchResultList.value = []
  all_su.value = 0
}

/**
 * 初期表示処理
 */
onMounted(async (): Promise<void> => {
  await getProjectNames()
  await getDcfCode()
})

</script>
<style scoped>
:deep(.pre-line) {
  white-space: pre-line !important;
}

:deep(.v-infinite-scroll--vertical) {
  display: flex;
  flex-direction: column;
  overflow-y: unset;
}

@media (max-width: 1904px) {
  :deep(.v-table > .v-table__wrapper > table > tbody > tr > td),
  :deep(.v-table > .v-table__wrapper > table > tbody > tr > td),
  :deep(.v-table > .v-table__wrapper > table > tbody > tr > th),
  :deep(.v-table > .v-table__wrapper > table > thead > tr > td),
  :deep(.v-table > .v-table__wrapper > table > thead > tr > th),
  :deep(.v-table > .v-table__wrapper > table > tfoot > tr > td),
  :deep(.v-table > .v-table__wrapper > table > tfoot > tr > th) {
    padding: 0 6px !important;
  }
}

@media (max-width: 600px) {
  :deep(.v-table > .v-table__wrapper > table > tbody > tr > td),
  :deep(.v-table > .v-table__wrapper > table > tbody > tr > td),
  :deep(.v-table > .v-table__wrapper > table > tbody > tr > th),
  :deep(.v-table > .v-table__wrapper > table > thead > tr > td),
  :deep(.v-table > .v-table__wrapper > table > thead > tr > th),
  :deep(.v-table > .v-table__wrapper > table > tfoot > tr > td),
  :deep(.v-table > .v-table__wrapper > table > tfoot > tr > th) {
    padding: 0 0.5px !important;
  }
}

@media (max-width: 600px) {
  .nouki_btn,
  .nouki_btn_outlined,
  .pt-0 {
    font-size: 13px;
    white-space: nowrap;
  }
}
</style>
