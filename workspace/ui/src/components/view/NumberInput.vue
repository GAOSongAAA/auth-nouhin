<template>
  <v-text-field
    type="number"
    hide-details="auto"
    variant="outlined"
    density="compact"
    :v-model="v_model"
    class="number_input"
  >
  </v-text-field>
</template>
<script setup lang="ts">
interface Props {
  v_model: string
}
defineProps<Props>()
</script>
