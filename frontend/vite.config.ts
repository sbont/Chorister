import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path';
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
      vue(),
      vueDevTools(),
  ],
  server: {
    port: 8091
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
})
