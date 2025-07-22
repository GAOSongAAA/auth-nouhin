<template>
  <c-snackbar
    ref="snackbar"
    color="red-accent-2"
    :timeout="-1"
    :close-on-back="false"
  >
    <template v-slot:default>
      <div class="d-flex justify-end float-right">
        <a @click="close" class="snackbar_close" translate="no">x</a>
      </div>
      <v-alert
        icon="$info"
        color="red-accent-2"
        :text="message"
        prominent
        class="errSnackBar"
      >
      </v-alert>
    </template>
  </c-snackbar>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import CSnackbar from '../control/CSnackbar.vue'
import { useRouter } from 'vue-router'
import messageList from '@/util/message.json'

/** スナックバー定義 */
const snackbar = ref<InstanceType<typeof CSnackbar>>()
/** エラーメッセージ */
const message = ref('')

/** ルータ */
const router = useRouter()

/**
 * 画面遷移時エラースナックバーを閉じる。
 *  @param newRoute
 */
watch(router.currentRoute, () => {
  close()
})

/**
 * 表示
 * @param msg エラーメッセージ
 */
function show(msg: string): void {
  // 表示文言を設定してから表示する
  message.value = msg
  snackbar.value!.isShow = true
}

/**
 * 表示 if ネットワーク関連エラー
 * @param nbErrCod 内部エラーコード
 * @returns true:正常、false:異常
 */
function showIfNetworkError(nbErrCod: string): boolean {
  //タイムアウト
  if (nbErrCod === '-1') {
    show(messageList.E000006)
    return false
  } else if (nbErrCod === '-3') {
    //ネットワークエラー
    show(messageList.E000007)
    return false
  }

  return true
}

/**
 * エラースナックバーを閉じる
 */
function close(): void {
  snackbar.value!.isShow = false
}

defineExpose({
  show,
  showIfNetworkError,
  close,
})
</script>
