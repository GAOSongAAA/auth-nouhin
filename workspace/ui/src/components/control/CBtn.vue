<template>
  <v-btn
    :class="class"
    :disabled="disabled"
    :loading="loading"
    @click="clickEvent"
  >
    {{ text }}
  </v-btn>
</template>

<script setup lang="ts">
import { ref, inject } from 'vue'
import { errorSnackbarKey } from '@/util/keys'

/** スナックバー定義 */
const snackbar = inject(errorSnackbarKey)

/**
 * props
 * Propの型は他ファイルからimportした型だと駄目らしい（公式より）
 */
interface Props {
  class?: string
  text: string
  disabled?: boolean
}

/**
 * クラス、表示FG　デフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  class: 'nouki_btn',
  disabled: false,
})

/**
 * 親コンポーネントのイベント処理を行う
 */
const emit = defineEmits<{
  (e: 'click'): void
}>()

/**
 * クリックイベント
 */
function clickEvent(): void {
  snackbar!.value.close()
  emit('click')
}

/** ローディングFG */
const loading = ref(false)

/**
 * ローディング開始
 */
function startLoading(): void {
  loading.value = true
}

/**
 * ローディング中チェック
 * @returns ローディング中かどうか
 */
function isLoading(): boolean {
  return loading.value
}

/**
 * ローディング終了
 */
function endLoading(): void {
  loading.value = false
}

defineExpose({
  startLoading,
  endLoading,
  isLoading,
})
</script>
