<template>
  <!-- ヘッダ部分 -->
  <Header
    :menu-view-fg="true"/>
  <!-- 検索結果 -->
  <v-container
    style="font-size: large"
    :class="'bg-grey-lighten-2'"
  >
    <v-row>
      <v-col>
        <v-card>
          <v-container>
              <v-row>
                <v-col>
                  <v-row>
                    <v-col cols="auto">企画名：{{ kikakuNm }}</v-col>
                    <v-spacer />
                  </v-row>
                  <v-row
                    ><v-col
                      >納品先：{{ nouhinNm }}（{{
                        tokCod.slice(0, 4) + '-' + tokCod.slice(4)
                      }}）</v-col
                    ></v-row
                  >
                  <v-row
                    ><v-col>伝票日付：{{ dnpDate }}</v-col></v-row
                  >
                  <v-row
                    ><v-col>伝票No.：{{ dnpNo }}</v-col></v-row
                  >
                  <v-row
                    ><v-col cols="auto" class="pr-0">商品/規格容量：</v-col
                    ><v-col class="px-0"
                      ><v-row
                        ><v-col>{{ kkkyryNm }}</v-col></v-row
                      ><v-row
                        ><v-col class="pt-0 px-0"
                          >（{{ kkkyryCod }}）</v-col
                        ></v-row
                      ></v-col
                    ></v-row
                  >
                  <v-row
                    ><v-col>数量：{{ hnbNum }}</v-col></v-row
                  >
                  <v-row
                    ><v-col>DCFコード：{{ prescriptionCode }}</v-col></v-row
                  >
                  <v-row
                    ><v-col cols="auto" class="pr-0">処方元：</v-col
                    ><v-col class="px-0"
                      ><v-row
                         ><v-col>
                        {{ unknownPrescriptionFlag ? '不明' + '　' + unknownPrescriptionReason_Text : prescriptionRykName }}
                        </v-col
                    　　></v-row
                  　  ></v-col
                　  ></v-row
                  >
                  <v-row v-if="drNameInputFlag"
                    ><v-col>処方医：{{unknownDrNameFlag ? '不明' :  drName }}</v-col
                    ></v-row
                  >
                  <v-row
                    ><v-col>診療科：{{unknownClinicFlag ? '不明' : clinic }}</v-col
                    ></v-row
                  >
                  <v-row
                  ><v-col>MRさまへの連絡事項：<br> 
                    <div style="margin-top: 8px;">{{ mrRenrakuZikou }}</div></v-col
                    ></v-row
                  >
                </v-col>
              </v-row>
              <v-row>
                <v-col class="d-flex flex-row"
                  ><c-btn
                    class="text-black nouki_btn_outlined"
                    text="一覧"
                    prependIcon=""
                    variant="outlined"
                    :disabled="false"
                    @click="moveToPrescriptionList()"
                /></v-col>
              </v-row>
          </v-container>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { Ref, ref, onMounted, inject } from 'vue'
import CBtn from '@/components/control/CBtn.vue'
import Header from '@/components/view/Header.vue'
import router from '@/router'

import {
  getPrescriptionDispKey,
  errorSnackbarKey,
} from '@/util/keys'
import { GetRequest, GetResponse } from '@/types/getPrescriptionDisp'

import messageList from '@/util/message.json'

import { PrescriptionList } from '@/router/names'

import { GetPrescriptionDispImpl } from '@/api/impl/getPrescriptionDisp'
import { getRequestParameterR } from '@/util/cookies'

// API
/** 詳細画面初期表示 */
const apiGetPrescriptionDisp = inject(
  getPrescriptionDispKey
) as GetPrescriptionDispImpl

/** スナックバー定義 */
const snackbar = inject(errorSnackbarKey)

/** 企画名 */
const kikakuNm: Ref<string> = ref('')

/** 納品先名 */
const nouhinNm: Ref<string> = ref('')

/** 得意先コード */
const tokCod: Ref<string> = ref('')

/** 伝票日付 */
const dnpDate: Ref<string> = ref('')

/** 伝票No. */
const dnpNo: Ref<string> = ref('')

/** 商品規格容量 */
const kkkyryNm: Ref<string> = ref('')

/** 商品コード */
const kkkyryCod: Ref<string> = ref('')

/** 販売数量 */
const hnbNum: Ref<string> = ref('')

/** DCFコード */
const prescriptionCode: Ref<string> = ref('')

/** 処方元名（正式） */
const prescriptionName: Ref<string> = ref('')

/** 処方元名（略式） */
const prescriptionRykName: Ref<string> = ref('')

/** 処方元不明フラグ */
const unknownPrescriptionFlag: Ref<boolean> = ref(false)

/** 処方元不明理由 */
const unknownPrescriptionReason_Text: Ref<string> = ref('')

/** 処方医 */
const drName: Ref<string> = ref('')

/** 処方医不明フラグ */
const unknownDrNameFlag: Ref<boolean> = ref(false)

/** 処方医入力可否フラグ */
const drNameInputFlag: Ref<boolean> = ref(false)

/** 診療科 */
const clinic: Ref<string> = ref('')

/** 診療科不明フラグ */
const unknownClinicFlag: Ref<boolean> = ref(false)

/** MRさまへの連絡事項 */
const mrRenrakuZikou: Ref<string> = ref('')

/** 初期表示時 */
onMounted(async (): Promise<void> => {
  await getPrescriptionDisp()
})

/** 詳細画面初期表示 */
const getPrescriptionDisp: () => Promise<void> = async () => {
  const request: GetRequest = {
    requestparameter: getRequestParameterR(),
  }
  const res: GetResponse = (await apiGetPrescriptionDisp.get(
    request
  )) as GetResponse

  if (res.nb_err_cod !== '200') {
    // ネットワーク関連エラー
    snackbar!.value.showIfNetworkError(res.nb_err_cod)
    if (res.nb_err_cod === '404') {
      // 検索結果が取得できない場合、スナックバーを表示する
      snackbar!.value.show(messageList.E000002)
    }
    return
  }
  //取得データを設定する
  kikakuNm.value = res.pre_kkk_nm
  nouhinNm.value = res.pre_nhn_nm
  tokCod.value = res.pre_tok_cod
  dnpDate.value = res.pre_dnp_ymd
  dnpNo.value = res.pre_dnp_no
  kkkyryNm.value = res.pre_syh_kkk_yry
  kkkyryCod.value = res.pre_syh_cod
  hnbNum.value = res.pre_hnb_num
  prescriptionCode.value = res.pre_dcf_cod
  prescriptionRykName.value = res.pre_nm
  unknownPrescriptionFlag.value = res.pre_cod === 1
  unknownPrescriptionReason_Text.value = res.pre_ryu
  drName.value = res.pre_dr
  unknownDrNameFlag.value = res.pre_dr_cod === 1
  drNameInputFlag.value = res.pre_dr_input_flg === 1
  clinic.value = res.pre_dp
  unknownClinicFlag.value = res.pre_dp_cod === 1
  mrRenrakuZikou.value = res.pre_mr_ren
}

/** 一覧画面に遷移 */
const moveToPrescriptionList = async () => {
    router.push(PrescriptionList)
  }

</script>
@/types/getPrescriptionDisp