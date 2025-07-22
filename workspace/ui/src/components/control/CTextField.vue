<template>
  <v-text-field
    hide-details="auto"
    variant="outlined"
    density="compact"
    v-model="input"
    :label="title"
    :placeholder="placeholder"
    :maxlength="maxlength"
    :disabled="disabled"
    :prepend-inner-icon="prependInnerIcon"
    @focus="emit('focus')"
    @change="emit('change')"
    @blur="emit('blur')"
    @update:model-value="emit('updateModel')"
    rows="1"
    auto-grow
  >
  </v-text-field>
</template>

<script setup lang="ts">
import { ref } from 'vue'

// リアクティブなdataの初期化方法
/**
 * props　項目
 */
interface Props {
  title?: string
  placeholder?: string
  maxlength?: string
  disabled?: boolean
  prependInnerIcon?: string
}

/**
 * props　デフォルト値
 */
withDefaults(defineProps<Props>(), {
  title: '',
  placeholder: '',
  maxlength: '100',
  disabled: false,
  prependInnerIcon: '',
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
const input = ref('')

defineExpose({
  input,
})
</script>
