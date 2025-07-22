<template>
  <c-btn :text="text" @click="submit()" />
</template>

<script setup lang="ts">
import CBtn from "@/components/control/CBtn.vue";

/**
 * props　項目
 */
interface Props {
  text: string;
}

/**
 * props　デフォルト値
 */
withDefaults(defineProps<Props>(), {
  text: "登録",
});

/**
 * 閉じるボタン押下イベント
 */
function submit(): void {}
</script>
