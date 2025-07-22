// Composables
import { createRouter, createWebHashHistory } from 'vue-router'
import * as Names from '@/router/names'
import * as Paths from '@/router/paths'

const routes = [
  // システムエラー画面
  {
    path: Paths.ERROR,
    name: Names.ERROR,
    component: () => import('../page/Error.vue'),
    meta: {
      title: 'ERROR',
      id: '005',
    },
  },

  // 詳細画面
  {
    path: Paths.PrescriptionDetail,
    name: Names.PrescriptionDetail,
    component: () => import('../page/PrescriptionDetail.vue'),
    meta: {
      title: '詳細画面',
    },
  },

  // 一覧画面
  {
    path: Paths.PrescriptionList,
    name: Names.PrescriptionList,
    component: () => import('../page/PrescriptionList.vue'),
    meta: {
      title: '一覧画面',
    },
    },
  
  // デフォルト→一覧画面
  {
    path: '',
    name: '',
    redirect:Paths.PrescriptionList,
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  next()
})

export default router
