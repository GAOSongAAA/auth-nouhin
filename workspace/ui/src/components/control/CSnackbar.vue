<template>
  <v-snackbar v-model="isShow" :timeout="timeout" class="snackBarSp">
    <slot></slot>
  </v-snackbar>
</template>

<script setup lang="ts">
import { ref } from 'vue'

/** 表示FG */
const isShow = ref(false)

/**
 * Props
 */
interface Props {
  timeout: number
}

/**
 * タイムアウト時間　デフォルト値
 */
withDefaults(defineProps<Props>(), {
  timeout: 3000,
})

defineExpose({
  isShow,
})
</script>
