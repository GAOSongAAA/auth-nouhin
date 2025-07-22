<template>
  <v-dialog v-model="dialogShow" class="d-flex align-start">
    <v-card class="my-2">
      <v-container>
        <v-row>
          <v-col class="d-flex justify-center" style="line-height: 1.6"
            >登録が完了していません。<br />
            修正内容が破棄されて<br />
            画面が移動します。
          </v-col>
        </v-row>
        <v-row class="d-flex justify-center">
          <v-col cols="auto"
            ><v-btn
              class="text-black nouki_btn_outlined"
              variant="outlined"
              @click="onclickCancel()"
              >キャンセル</v-btn
            ></v-col
          >
          <v-col cols="auto" class="d-flex justify-end"
            ><c-btn
              class="nouki_btn"
              text="OK"
              prependIcon=""
              color=""
              :disabled="false"
              @click="onclickOk()"
          /></v-col>
        </v-row>
      </v-container>
    </v-card>
  </v-dialog>
</template>

<script setup lang="ts">
import CBtn from '@/components/control/CBtn.vue'
import { onMounted, watch } from 'vue'

/**
 * props　表示項目
 */
interface Props {
  dialogShowProp: boolean
}
defineProps<Props>()

/** ダイアログ開閉フラグ */
const dialogShow = defineModel<boolean>('dialogShowProp')

const onclickOk = () => {
  resolve('confirm')
  dialogShow.value = false
}

const onclickCancel = () => {
  resolve('cancel')
  dialogShow.value = false
}

let resolve: (action: 'cancel' | 'confirm') => void

const openDialog: () => Promise<'cancel' | 'confirm'> = async () => {
  dialogShow.value = true
  return await new Promise<'cancel' | 'confirm'>((res) => {
    resolve = res
  })
}

defineExpose({
  openDialog,
})

watch(dialogShow, (newVal) => {
  if (!newVal && resolve) {
    resolve('cancel')
  }
})
</script>
