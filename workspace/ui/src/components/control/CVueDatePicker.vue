<template>
  <div class="custom_vue_datetimepicker">
    <VueDatePicker
      class=""
      :class="error_message !== '' ? 'datepicker_error' : ''"
      style=""
      input-class-name=""
      show-now-button
      now-button-label="今日"
      :enable-time-picker="false"
      :placeholder="props.placeholder"
      position="left"
      auto-apply
      locale="ja"
      format="yyyy/MM/dd"
      v-model="input"
      model-type="yyyyMMdd"
      @update:model-value="emit('update:model-value')"
      :day-class="getDayClass"
    />
    <p v-if="error_message !== ''" class="error_message">{{ error_message }}</p>
  </div>
</template>

<script setup lang="ts">
import VueDatePicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import { ref } from 'vue'
import { getDayClass } from '@/util/setColor'

/**
 * Propsの型
 */
interface Props {
  placeholder: string
}

/**
 * Propsのデフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  placeholder: '',
})

/**
 * イベント一覧
 */
const emit = defineEmits<{
  (e: 'update:model-value'): void
}>()

/** 入力値 */
const input = defineModel<string>('date')

/** エラーメッセージ */
const error_message = ref<string>('')
</script>
<style lang="css" scoped>
.dp__theme_light {
  --dp-border-color: #aaaeb7;
  --dp-menu-border-color: #aaaeb7;
  --dp-border-color-hover: #000;
  --dp-border-color-focus: #000;
}
</style>

<style lang="css" scoped>
@media (max-width: 600px) {
  :deep(.dp__pointer.dp__input_readonly.dp__input.dp__input_icon_pad.dp__input_reg::placeholder) {
    font-size: 11px !important;
  }
}
</style>
