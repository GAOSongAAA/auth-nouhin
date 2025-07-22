<template>
  <v-textarea
    hide-details="auto"
    variant="outlined"
    density="compact"
    v-model="input"
    :label="label"
    :placeholder="placeholder"
    :maxlength="maxlength"
    :disabled="disabled"
    :prepend-inner-icon="prependInnerIcon"
    @focus="emit('focus')"
    @change="emit('change')"
    @blur="emit('blur')"
    @update:model-value="emit('updateModel')"
    @input="deleteLineBreak"
    rows="1"
    auto-grow
  >
  </v-textarea>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// リアクティブなdataの初期化方法
/**
 * props　項目
 */
interface Props {
  label?: string
  placeholder?: string
  maxlength?: string
  disabled?: boolean
  prependInnerIcon?: string
  deleteLineBreakFlag?: boolean
}

/**
 * props　デフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  label: '',
  placeholder: '',
  maxlength: '100',
  disabled: false,
  prependInnerIcon: '',
  deleteLineBreakFlag: true,
})

/**
 * 親コンポーネントのイベント処理を行う
 */
const emit = defineEmits<{
  (e: 'focus'): void
  (e: 'change'): void
  (e: 'blur'): void
  (e: 'updateModel'): void
}>()

/** 入力値 */
const input = defineModel<string>('input')

/** 改行を削除 */
const deleteLineBreak = () => {
  if (props.deleteLineBreakFlag) {
    input.value = input.value?.replace(/\r?\n/g, '')
  }
}
</script>

<style lang="css" scoped>
@media (max-width: 600px) {
  :deep(.v-field__input::placeholder) {
    font-size: 11px !important;
  }
}
</style>