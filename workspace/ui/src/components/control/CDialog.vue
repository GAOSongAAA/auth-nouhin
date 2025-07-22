<template>
  <v-dialog
    :class="!mobile ? 'dialog' : 'app_display_dialog'"
    v-model="confirmDialogShow"
    :persistent="persistentFg"
  >
    <slot name="default">
      <v-card>
        <span>コンポーネントが何も選択されていません</span>
      </v-card>
    </slot>
  </v-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useDisplay } from 'vuetify/lib/framework.mjs'

const { mobile } = useDisplay()

/**
 * props　項目
 */
interface Props {
  persistentFg: boolean
}
/**
 * props　デフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  persistentFg: true,
})

/** ダイアログ表示FG */
const confirmDialogShow = ref<boolean>(false)

/**
 * ダイアログを表示する
 */
function show(): void {
  confirmDialogShow.value = true
}

/**
 * ダイアログを非表示にする
 * @param searchFg 検索FG
 */
function closeDialog(): void {
  // ダイアログを閉じる
  confirmDialogShow.value = false
}

defineExpose({
  show,
  closeDialog,
})
</script>
