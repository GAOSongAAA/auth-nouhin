<template>
  <div class="error">
    <div class="error_inner">
      <div class="error_content">
        <p class="error_ttl">システムエラー</p>
        <p class="error_text" style="font-weight: bold">
          予期しないエラーが発生しました。<br />ご迷惑をお掛けしますがログインしなおしてください。
        </p>
        <p class="error_text" style="font-weight: bold">
          エラーコード:{{ code }}
        </p>
        <c-btn class="error_button" text="戻る" @click="onclickBack()"></c-btn>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import CBtn from '../components/control/CBtn.vue'
import { Ref, ref, onMounted, inject } from 'vue'
import { setParams } from '@/util/errCodeParam'
import { deleteCookiesWhenLogout, getLogoutURL } from '@/util/cookies'

/** ログアウトURL */
const logoutUrl: Ref<string> = ref<string>('')
/** 画面表示コード */
const code = ref<string | unknown>('')
/** エラーコード */
const errCode = ref<string | unknown>('')
/** 前画面のURL */
const prevRoute = ref<string>('')

/**
 * 戻るボタン押下処理
 */
function onclickBack(): void {
  // CookieからログアウトURLを取得する
  logoutUrl.value = getLogoutURL()
  // Cookieを削除する
  deleteCookiesWhenLogout()
  //内部エラーコードが999999の場合、ログイン画面に遷移する
　if (errCode.value = '999999') {
     window.location.href = location.protocol + '//' + location.host;
　// ログアウトURLへ遷移
  } else {
     window.location.href = logoutUrl.value;
  }
}

//前画面のURL設定
const setPathFrom = (path: string) => {
  prevRoute.value = path
}

/**
 * 初期表示
 */
onMounted(async (): Promise<void> => {
  //エラーコードを取得する
  errCode.value = setParams.errCode

  //エラーコードが取得できない場合
  if (setParams.errCode === '') {
    code.value = '999999'
    return
  }

  code.value = errCode.value

  // //パラメータを初期化する。
  // setParams.errCode = ''

  // //リファラを取得する
  // const ref = prevRoute.value

  // // リファラ文字列を判別
  // if (ref.length == 0) {
  //   return
  // }

  // code.value = ref + errCode.value
})

defineExpose({ setPathFrom })
</script>

<script lang="ts">
import { defineComponent, ComponentPublicInstance } from 'vue'

/**
 * 前画面のパス
 */
interface IInstance extends ComponentPublicInstance {
  setPathFrom(from: string): void
}

export default defineComponent({
  /**
   * 前画面のパスを取得する
   *  @param to 次のルート
   *  @param from 前のルート
   *  @param next パイプラインの次のフックに移動する関数
   */
  beforeRouteEnter(to, from, next) {
    next((vm) => {
      const instance = vm as IInstance
      const beforeId = from.meta.id !== '' ? from.meta.id : '999'
      instance.setPathFrom(String(beforeId))
    })
  },
})
</script>
