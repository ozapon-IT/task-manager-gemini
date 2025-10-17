<template>
  <div class="bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100 min-h-screen">
    <header class="border-b border-gray-200 dark:border-gray-800">
      <div class="container mx-auto px-4">
        <nav class="flex items-center justify-between h-16">
          <div class="flex items-center">
            <NuxtLink to="/" class="text-xl font-bold">Task Manager</NuxtLink>
          </div>
          <div class="flex items-center space-x-4">
            <NuxtLink to="/" class="hover:text-gray-700 dark:hover:text-gray-300">Dashboard</NuxtLink>
            <NuxtLink to="/projects" class="hover:text-gray-700 dark:hover:text-gray-300">Projects</NuxtLink>
            <NuxtLink to="/ping" class="hover:text-gray-700 dark:hover:text-gray-300">Ping</NuxtLink>
          </div>
          <div class="flex items-center">
            <button @click="toggleColorMode" class="p-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-800">
              <component :is="colorMode.value === 'dark' ? SunIcon : MoonIcon" class="h-6 w-6" />
            </button>
          </div>
        </nav>
      </div>
    </header>
    <main class="container mx-auto px-4 py-8">
      <NuxtPage />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { SunIcon, MoonIcon } from 'lucide-vue-next'

const colorMode = useColorMode()

const toggleColorMode = () => {
  colorMode.preference = colorMode.value === 'dark' ? 'light' : 'dark'
}

// Since we are using client-side rendering for the color mode, we need to ensure the component is mounted before rendering the icon.
const SunIcon = ref(null)
const MoonIcon = ref(null)

onMounted(async () => {
  const lucide = await import('lucide-vue-next')
  SunIcon.value = lucide.SunIcon
  MoonIcon.value = lucide.MoonIcon
})

</script>

<style>
.dark-mode body {
  background-color: #1a202c;
  color: #edf2f7;
}
</style>