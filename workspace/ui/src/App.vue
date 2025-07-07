<template>
  <v-app>
    <v-main class="content">
      <RouterView />
    </v-main>
    <error-snackbar ref="errorSnackbar" />
    <info-snackbar ref="infoSnackbar" />
  </v-app>
</template>

<script setup lang="ts">
import { onMounted, provide, ref, watch } from 'vue'
import { RouteLocationNormalizedLoaded, RouterView, useRoute, useRouter } from 'vue-router'
import ErrorSnackbar from './components/view/ErrorSnackbar.vue'
import InfoSnackbar from './components/view/InfoSnackbar.vue'
import { errorSnackbarKey, infoSnackbarKey } from './util/keys'
import { getMoveURL } from '@/util/cookies'
import { moveURL_Error } from '@/util/constant'
import {
  ERROR,
} from '@/router/paths'

/** エラースナックバー定義 */
const errorSnackbar = ref<InstanceType<typeof ErrorSnackbar>>(
  undefined as unknown as InstanceType<typeof ErrorSnackbar>
)
provide(errorSnackbarKey, errorSnackbar)
/** インフォスナックバー定義 */
const infoSnackbar = ref<InstanceType<typeof InfoSnackbar>>(
  undefined as unknown as InstanceType<typeof InfoSnackbar>
)
provide(infoSnackbarKey, infoSnackbar)
/** ルーター */
const route = useRoute()
const router = useRouter()

/**
 * タイトルの設定を行う
 * @param routeInstance
 */
const setTitle = (routeInstance: RouteLocationNormalizedLoaded) => {
  if (routeInstance.meta.title) {
    // タイトルがrouter.jsに設定されている場合。
    document.title = '処方元情報サービス/' + routeInstance.meta.title
  } else {
    // タイトルがrouter.jsに設定されていない場合。
    document.title = '処方元情報サービス'
  }
}

/**
 * タイトルの設定を行う
 *  @param newRoute
 */
watch(route, (newRoute) => {
  setTitle(newRoute)
})

/**
 * 初期表示
 */
onMounted(() => {
  switch (getMoveURL()) {
    case moveURL_Error:
      router.push(ERROR)
      break
    default:
      break
  }
  setTitle(route)
})
</script>