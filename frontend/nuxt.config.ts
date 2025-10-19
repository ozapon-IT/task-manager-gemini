// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  compatibilityDate: '2025-07-15',
  devtools: { enabled: true },
  modules: ['@nuxtjs/tailwindcss', '@pinia/nuxt', '@nuxtjs/color-mode'],
  colorMode: {
    classSuffix: ''
  },
  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/api',
    },
  },
  vite: {
    server: {
      proxy: {
        '/api': {
          target: 'http://backend:8080',
          changeOrigin: true,
        },
      },
    },
  },
})
