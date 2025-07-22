<template>
  <div>
    <!-- テーブル -->
    <div ref="parent" v-resize="tableResize">
      <c-data-table
        ref="loadingTable"
        class="infinite_scroll_table"
        items-per-page="-1"
        :headers="mobile && !useMobileHeaders ? [] : headers"
        :items="items"
        :height="height"
      >
        <template v-for="(value, name) in $slots" v-slot:[name]>
          <slot :name="name" />
        </template>
        <template v-slot:body>
          <slot name="body">
            <tr v-for="item in items">
              <td
                v-for="header in headers"
                :key="header.key"
                :align="header.tdAlign"
              >
                {{ item[header.key as keyof typeof item] }}
              </td>
            </tr>
          </slot>
        </template>
        <template v-slot:bottom></template>
      </c-data-table>
    </div>
    <!-- フロートボタン -->
    <div v-show="mobile" class="floating_action_button">
      <v-fab-transition>
        <v-btn
          v-show="isShowFab"
          class="mt-auto pointer-events-initial"
          color="primary"
          elevation="8"
          :icon="mdiChevronUp"
          size="large"
          @click="onClickFab"
        />
      </v-fab-transition>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useDisplay } from 'vuetify';
import CDataTable from '@/components/control/CDataTable.vue';
import { Headers, ItemsBase } from '../../types/table';
import { mdiChevronUp  } from '@mdi/js'

/**
 * props　項目
 */
interface Props {
  useMobileHeaders: boolean;
  headers: Array<Headers>;
  items: Array<ItemsBase>;
  all_su: number;
  end_row: number;
  init_height: number;
}

/**
 * props　デフォルト値
 */
const props = withDefaults(defineProps<Props>(), {
  init_height: -1,
  useMobileHeaders: false,
});

/**
 * 親コンポーネントのイベント処理を行う
 */
const emits = defineEmits<{
  (e: 'scrollend'): void
}>()

/** モバイルFG */
const { mobile } = useDisplay()

/** ローディングFG */
const loading = ref<boolean>(false)
/** テーブルの高さ */
const height = ref<number | string>(300)
/** フロートボタン表示FG */
const isShowFab = ref<boolean>(false)
/** 親要素 */
const parent = ref<HTMLDivElement | null>(null);
/** スクロールイベント */
const scrollDiv = ref<Element>()!;
const loadingTable = ref<InstanceType<typeof CDataTable>>();

/**
 * スクロールの読み込みをスクロール末尾の何ピクセル前から起こすか。
 * 0にするとまれにピクセルの高さが合わない場合がある。
*/
const INTERVAL: number = 30;

/**
 * どれくらいスクロールしたらFabを表示するか
 */
const SHOW_FAB_SCROLL_HEIGHT = 200;

/**
 * DOMマウント時に呼ばれるイベント
 */
onMounted(async () => {
  // Fabボタン表示用のスクロールイベントを登録しておく
  scrollDiv.value = parent.value?.children.item(0)?.children.item(0)!;
  scrollDiv.value.addEventListener("scroll", onFabScroll);
})

/**
 * データ追加後にデータ追加イベントを再呼出し可能にする
 */
watch(props, () => {
  // init_heightの変更があった場合、init_height
  if (isHeightSet()) {
    setHeight();
  }
  scrollDiv.value = parent.value?.children.item(0)?.children.item(0)!;
  // tableのスクロール部分からスクロールイベントを削除する
  scrollDiv.value.removeEventListener("scroll", onTableScroll);

  // end_rowがall_suがより小さい場合スクロールイベントを追加する
  if (props.end_row < props.all_su) {
    scrollDiv.value.addEventListener("scroll", onTableScroll);
  }
  loading.value = false;
})

/**
 * 末尾までスクロール時にデータを追加するイベントを送る
 * @param event イベント
 */
const onTableScroll = (event: Event) => {
  if (
    scrollDiv.value?.scrollTop! + scrollDiv.value?.clientHeight! >=
      scrollDiv.value?.scrollHeight! - INTERVAL &&
    !loading.value
  ) {
    loading.value = true
    emits("scrollend")
  }
}

/**
 * 表の高さを動的に変更する
 * ※サイズチェンジで直接呼ばれる関数
 */
const tableResize = () => {
  if (!isHeightSet()) {
    // デバイスのサイズによって高さを切り替える
    const tableHeight = getHeight()
    height.value = tableHeight
  }
}

/**
 * init_heightをテーブルの高さとして設定する。
 */
function setHeight() {
  height.value = props.init_height;
}

/**
 * テーブルの高さを算出する
 * @returns テーブルの高さ
 */
function getHeight(): number {
  // 全体-(Top+Bottom)
  return window.innerHeight - (parent.value!.offsetTop + 12)
}

/**
 * 表の高さを変更する
 * ※コードから呼ぶ場合はこちらを使用する
 */
async function resizeTableHeight(): Promise<void> {
  await new Promise(r => setTimeout(r, 0.1))
  tableResize()
}

/**
 * デバイス回転時に表の高さを変更する
 */
window.addEventListener("orientationchange", resizeTableHeight);

/**
 * スクロール時イベント（Fabボタン用）
 * @param event イベント
 */
function onFabScroll(event: Event): void {
  // ちょっとスクロールされたらボタンを表示する
  isShowFab.value = scrollDiv.value!.scrollTop > SHOW_FAB_SCROLL_HEIGHT
}

/**
 * テーブルの上までスクロール
 */
function onClickFab(): void {
  scrollDiv.value!.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

/**
 * ローディングを開始する
 */
function startLoading(): void {
  loadingTable.value!.startLoading();
}

/**
 * ローディングを終了する
 */
function endLoading(): void {
  // ローディングを終了
  loadingTable.value!.endLoading();
  // テーブルの高さを算出する
  resizeTableHeight()
}

/**
 * ローディング中かどうか出力する
 */
function isLoading(): boolean {
  return loadingTable.value!.isLoading();
}

/**
 * 引数のinit_heightが設定されている場合はtrue、設定されていなければfalseを返す。
 */
function isHeightSet(): boolean {
  return props.init_height !== -1
}

/**
 * スクロール位置取得
 * @returns スクロール位置
 */
function getScrollpix(): number {
  // スクロールされているピクセル数を取得
  var elemTop = scrollDiv.value!.scrollTop;
  return elemTop;
}

/**
 * スクロール位置移動
 * @param scrollPix スクロール位置
 */
function moveScroll(scrollPix: number): void {
  scrollDiv.value!.scrollTop = scrollPix;
}

defineExpose({
  startLoading,
  endLoading,
  isLoading,
  resizeTableHeight,
  getScrollpix,
  moveScroll,
});
</script>