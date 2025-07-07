<template>
  <v-toolbar class="pl-3 hattyu_header" density="compact">
    <span class="header_ttl">処方元情報連携システム</span>
    <slot name="items"></slot>
    <v-spacer></v-spacer>
    <v-app-bar-nav-icon
      @click.stop="drawer = !drawer"
      :icon="mdiDotsHorizontal"
      class="icon_white"
    ></v-app-bar-nav-icon>
  </v-toolbar>
  <v-navigation-drawer
    v-if="menuViewFg"
    v-model="drawer"
    bottom
    temporary
    location="right"
  >
    <v-list class="rounded-0 menu_item">
      <v-list-item
        v-for="(item, index) in items"
        :key="index"
        :to="item.link"
        :href="item.href"
        :class="item.class"
        :value="item"
        @click="item.onClick"
      >
        <div v-if="index === 0">
          <v-list-item-title class="header_menu_close d-flex justify-end">
            <img
              src="@/assets/header_menu_close.png"
              alt=""
              width="20"
              height="26"
          /></v-list-item-title>
        </div>
        <div v-else>
          <v-list-item-title
            v-text="item.title"
            class="header_menu_item"
          ></v-list-item-title>
        </div>
      </v-list-item>
    </v-list>
    <template v-slot:append>
      <div>
        <p class="ver_text">V1.2.0</p>
      </div>
    </template>
  </v-navigation-drawer>
  <DiscardConfirmDialog
    ref="discardConfirmDialog"
    v-model:dialogShowProp="discardUpdateModeConfirmDialog"
  ></DiscardConfirmDialog>
</template>

<script setup lang="ts">
import { Ref, ref, onMounted, inject } from 'vue'
import { useDisplay } from 'vuetify'
import DiscardConfirmDialog from './DiscardConfirmDialog.vue'
import { mdiDotsHorizontal } from '@mdi/js'
import {
  deleteCookiesWhenLogout,
  setPasswordChangeURL,
  getPasswordChangeURL,
  setLogoutURL,
  getLogoutURL,
} from '@/util/cookies'
import * as Paths from '@/router/paths'
import router from '@/router'
import { getSystemSettingsKey } from '@/util/keys'
import { GetSystemSettingsImpl } from '@/api/impl/getSystemSettings'
import { GetResponse } from '@/types/getSystemSettings'

//API
/** システム設定値取得 */
const apiGetSystemSettings = inject(
  getSystemSettingsKey
) as GetSystemSettingsImpl

/** パスワード変更URL */
const passChangeUrl: Ref<string> = ref<string>('')
/** ログアウトURL */
const logoutUrl: Ref<string> = ref<string>('')

/** メニュー表示FG */
const drawer = ref<boolean>(false)
/** モバイルFG */
const { mobile } = useDisplay()

/** 遷移確認ダイアログ開閉フラグ */
const discardUpdateModeConfirmDialog: Ref<boolean> = ref<boolean>(false)

const discardConfirmDialog = ref<InstanceType<
  typeof DiscardConfirmDialog
> | null>(null)

/**
 * props　項目
 */
interface Props {
  /** メニュー表示フラグ */
  menuViewFg?: boolean
  /** 画面遷移時に確認ダイアログを開くフラグ */
  openConfirmDialogFlag?: boolean
}
/**
 * props　デフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  /** メニュー表示フラグ */
  menuViewFg: false,
  /** 画面遷移時に確認ダイアログを開くフラグ */
  openConfirmDialogFlag: false,
})

/** 一覧画面に遷移 */
const onclickPageMenu = async (path: string) => {
  // 確認ダイアログを開くフラグがtrueの場合は、確認ダイアログを開いてOKが押された場合に画面遷移
  if (props.openConfirmDialogFlag) {
    const confirmDialogResult = await discardConfirmDialog.value!.openDialog()
    if (confirmDialogResult === 'confirm') {
      transitionPage(path)
    }
  } else {
    // falseの場合は、そのまま画面遷移
    transitionPage(path)
  }
}

/** 画面遷移 */
const transitionPage = (path: string) => {
  router.push({ path: path })
}

/**
 * メニューを閉じる
 */
const closeMenu = (): void => {
  drawer.value = false
}

/** 型 メニュー項目 */
type MenuItem = {
  title: string
  link: string
  href: string
  class: string
  path?: string
  onClick: () => void
}

/** メニュー項目 メニュー閉じる */
const item_CloseMenu: MenuItem = {
  title: '',
  link: '',
  href: '',
  class: '',
  onClick: closeMenu,
}

/** メニュー項目 一覧画面 */
const item_PrescriptionList: MenuItem = {
  title: '一覧画面',
  link: '',
  href: '',
  class: 'no_underline',
  path: Paths.PrescriptionList,
  onClick: () => {
    return onclickPageMenu(Paths.PrescriptionList)
  },
}

/** メニュー項目 ログアウト */
const item_Logout: MenuItem = {
  title: 'ログアウト',
  link: '',
  href: '',
  class: '',
  onClick: logout,
}

/** メニュー */
const items: Ref<Array<MenuItem>> = ref([
  item_CloseMenu,
  item_PrescriptionList,
  item_Logout,
])

/**
 * 初期表示
 */
onMounted(async (): Promise<void> => {
  // Cookieにパスワード変更URLとログアウトURLが設定ありの場合
  if (getPasswordChangeURL() && getLogoutURL()) {
    passChangeUrl.value = getPasswordChangeURL()
    logoutUrl.value = getLogoutURL()
  } else {
    // システム設定値取得
    getSystemSettings()
  }
})

/** システム設定値取得 */
const getSystemSettings: () => Promise<void> = async () => {
  const res: GetResponse = (await apiGetSystemSettings.get()) as GetResponse

  if (res.nb_err_cod !== '200' || !res.pass_change_url || !res.logout_url) {
    // システムエラー画面遷移
    router.push(Paths.ERROR)
    return
  }
  // 取得データを設定する
  passChangeUrl.value = res.pass_change_url
  logoutUrl.value = res.logout_url
  // Cookieに設定する
  setPasswordChangeURL(res.pass_change_url)
  setLogoutURL(res.logout_url)
}

/**
 * ログアウトリンク押下処理
 */
function logout(): void {
  deleteCookiesWhenLogout()
  window.location.href = logoutUrl.value
}
</script>
<style lang="css" scoped>
@import '../../styles/style_Header';
</style>
