<template>
  <div>
    <h1 class="text-3xl font-bold mb-8">Ping API Test</h1>
    <div class="flex items-center space-x-4">
      <button @click="ping" class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg">
        Ping Test
      </button>
      <div v-if="loading">Loading...</div>
      <div v-if="response" class="flex items-center">
        <span v-if="response.pong" class="text-green-500 flex items-center">
          <CheckCircleIcon class="h-6 w-6 mr-2" /> Pong!
        </span>
        <span v-else class="text-red-500 flex items-center">
          <XCircleIcon class="h-6 w-6 mr-2" /> Error!
        </span>
      </div>
      <div v-if="error" class="text-red-500">
        <p>Error: {{ error.message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { CheckCircleIcon, XCircleIcon } from 'lucide-vue-next'
import axios from '~/plugins/axios'

const loading = ref(false)
const response = ref<{ pong: boolean } | null>(null)
const error = ref<Error | null>(null)

const ping = async () => {
  loading.value = true
  response.value = null
  error.value = null
  try {
    const res = await axios.get('/api/ping')
    response.value = res.data
  } catch (e) {
    error.value = e as Error
  } finally {
    loading.value = false
  }
}
</script>
