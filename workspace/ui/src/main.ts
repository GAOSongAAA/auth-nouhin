/**
 * main.ts
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'

// Plugins
import { registerPlugins } from '@/plugins'

// CSS
import './styles/style.css'

// DI
import {
  getPrescriptionDispKey,
  getProjectNamesKey,
  getDcfCodeKey,
  getPrescriptionListSearchKey,
  getSystemSettingsKey,
} from './util/keys'

import { GetPrescriptionDispImpl } from './api/impl/getPrescriptionDisp'
import { GetProjectNamesImpl } from './api/impl/getProjectNames'
import { GetDcfCodeImpl } from './api/impl/getDcfCode'
import { GetPrescriptionListSearchImpl } from './api/impl/getPrescriptionListSearch'
import { GetSystemSettingsImpl } from './api/impl/getSystemSettings'

const app = createApp(App)

// DI注入
app.provide(getPrescriptionDispKey, new GetPrescriptionDispImpl())
app.provide(getProjectNamesKey, new GetProjectNamesImpl())
app.provide(getDcfCodeKey, new GetDcfCodeImpl()) 
app.provide(getPrescriptionListSearchKey, new GetPrescriptionListSearchImpl())
app.provide(getSystemSettingsKey, new GetSystemSettingsImpl())

registerPlugins(app)

app.mount('#app')
