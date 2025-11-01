// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'
import path from 'path' // <-- Add this import

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(),  tailwindcss(),],
  resolve: {
    alias: {
      // Configure the '@' alias to point to the 'src' directory
      '@': path.resolve(__dirname, './src') 
    }
  }
})