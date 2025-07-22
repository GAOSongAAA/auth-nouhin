// Plugins
import vue from '@vitejs/plugin-vue'
import vuetify, { transformAssetUrls } from 'vite-plugin-vuetify'

// Utilities
import { defineConfig } from 'vite'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: { transformAssetUrls },
    }),
    // https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vite-plugin
    vuetify({
      autoImport: true,
    }),
  ],
  define: { 
    'process.env': {},
    'import.meta.env.INDEX_PAGE': JSON.stringify(process.env.INDEX_PAGE || 'http://localhost:8080/index.html')
 },
  base: '/',
  publicDir: './public/',
  build: {
    rollupOptions: {
      output: {
        assetFileNames: 'assets/ui/[name]-[hash][extname]',
        chunkFileNames: 'assets/ui/[name]-[hash].js',
        entryFileNames: 'assets/ui/[name]-[hash].js',
      },
    },
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
    extensions: ['.js', '.json', '.jsx', '.mjs', '.ts', '.tsx', '.vue'],
  },
  server: {
    port: 3000,
    watch: {
      usePolling: true,
      interval: 1000
    }
  },
})
