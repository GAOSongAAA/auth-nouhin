<template>
  <v-select
    hide-details="auto"
    bg-color="white"
    variant="outlined"
    density="compact"
    v-model="selectedItem"
    :label="title"
    :placeholder="placeholder"
    :items="items"
    :disabled="disabled"
    @update:model-value="emit('update:model-value')"
  >
  </v-select>
</template>

<script setup lang="ts">
import { SelectsBase } from '../../types/selects'

/**
 * props　項目
 */
interface Props {
  title: string
  placeholder?: string
  items: Array<SelectsBase>
  disabled?: boolean
}

/**
 * props　デフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  title: '',
  placeholder: '',
  disabled: false,
})

/**
 * 親コンポーネントのイベント処理を行う
 */
const emit = defineEmits<{
  (e: 'update:model-value'): void
}>()

/**
 * 選択中の項目（valueが設定される）
 */
const selectedItem = defineModel<string>('selectedItem')
</script>

<style lang="css" scoped>
.v-select,
.v-select * {
  /* セレクトボックスの高さ */
  --v-input-control-height: 36px !important;
  /* セレクトボックスの文字表示部分の上下のパディングの値 */
  --v-input-padding-top: 6px !important;
  --v-field-padding-bottom: 6px !important;
}
</style>
