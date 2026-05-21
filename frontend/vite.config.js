import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
  resolve: {
    alias: {
      // Dòng này cực kỳ quan trọng: Định nghĩa '@' chính là thư mục 'src'
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})