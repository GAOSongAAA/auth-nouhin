<template>
  <c-snackbar ref="snackbar" :timeout="2000" color="light-blue-accent-3">
    <template v-slot:default>
      <v-alert type="info" :text="message" color="light-blue-accent-3">
      </v-alert>
    </template>
  </c-snackbar>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import CSnackbar from '../control/CSnackbar.vue'

/** スナックバー定義 */
const snackbar = ref<InstanceType<typeof CSnackbar>>()
/** メッセージ */
const message = ref<string>('')

/**
 * 表示
 * @param msg メッセージ
 */
const show = (msg: string): void => {
  // 表示文言を設定してから表示する
  message.value = msg
  snackbar.value!.isShow = true
}

defineExpose({
  show,
})
</script>
