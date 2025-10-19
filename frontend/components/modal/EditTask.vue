<template>
  <div v-if="show" class="fixed z-10 inset-0 overflow-y-auto">
    <div class="flex items-center justify-center min-h-screen pt-4 px-4 pb-20 text-center sm:block sm:p-0">
      <div class="fixed inset-0 transition-opacity" aria-hidden="true">
        <div class="absolute inset-0 bg-gray-500 opacity-75"></div>
      </div>

      <span class="hidden sm:inline-block sm:align-middle sm:h-screen" aria-hidden="true">&#8203;</span>

      <div class="inline-block align-bottom bg-white dark:bg-gray-800 rounded-lg text-left overflow-hidden shadow-xl transform transition-all sm:my-8 sm:align-middle sm:max-w-lg sm:w-full">
        <form @submit.prevent="save">
          <div class="bg-white dark:bg-gray-800 px-6 py-8 sm:p-8">
            <h3 class="text-xl leading-6 font-medium text-gray-900 dark:text-gray-100 mb-6">{{ task && task.id ? 'Edit Task' : 'New Task' }}</h3>
                        <div class="space-y-6">
                          <div>
                            <label for="title" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Title</label>
                            <input v-model="form.title" type="text" id="title" class="block w-full shadow-sm sm:text-sm border-gray-300 rounded-md dark:bg-gray-700 dark:border-gray-600 px-3 py-2" required>
                          </div>
                          <div>
                            <label for="description" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Description</label>
                            <textarea v-model="form.description" id="description" rows="4" class="block w-full shadow-sm sm:text-sm border-gray-300 rounded-md dark:bg-gray-700 dark:border-gray-600 px-3 py-2" required></textarea>
                          </div>
                          <div>
                            <label for="dueDate" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Due Date</label>
                            <input v-model="form.dueDate" type="date" id="dueDate" class="block w-full shadow-sm sm:text-sm border-gray-300 rounded-md dark:bg-gray-700 dark:border-gray-600 px-3 py-2" required>
                          </div>
                          <div>
                            <label for="priority" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Priority</label>
                            <select v-model="form.priority" id="priority" class="block w-full shadow-sm sm:text-sm border-gray-300 rounded-md dark:bg-gray-700 dark:border-gray-600 px-3 py-2" required>
                              <option value="LOW">Low</option>
                              <option value="MEDIUM">Medium</option>
                              <option value="HIGH">High</option>
                            </select>
                          </div>
                          <div>
                            <label for="status" class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Status</label>
                            <select v-model="form.status" id="status" class="block w-full shadow-sm sm:text-sm border-gray-300 rounded-md dark:bg-gray-700 dark:border-gray-600 px-3 py-2" required>
                              <option value="TODO">To Do</option>
                              <option value="IN_PROGRESS">In Progress</option>
                              <option value="DONE">Done</option>
                            </select>
                          </div>
                        </div>
                      </div>
                      <div class="bg-gray-50 dark:bg-gray-900 px-6 py-4 sm:px-8 sm:flex sm:flex-row-reverse">
                        <button type="submit" class="w-full inline-flex justify-center rounded-md border border-transparent shadow-sm px-6 py-2 bg-blue-600 text-base font-medium text-white hover:bg-blue-700 sm:ml-3 sm:w-auto sm:text-sm">Save</button>
                        <button @click="close" type="button" class="mt-3 w-full inline-flex justify-center rounded-md border border-gray-300 shadow-sm px-6 py-2 bg-white text-base font-medium text-gray-700 hover:bg-gray-50 dark:bg-gray-700 dark:text-gray-200 dark:hover:bg-gray-600 sm:mt-0 sm:w-auto sm:text-sm">Cancel</button>
                      </div>
                    </form>
                  </div>
                </div>
              </div>
            </template>
            
            <script setup lang="ts">
            import { ref, watch, defineProps, defineEmits } from 'vue'
            import type { Task, TaskStatus, TaskPriority } from '~/types/api'
            
            const props = defineProps<{ show: boolean; task: Task | null; projectId?: number | null }>()
            const emit = defineEmits(['close', 'save'])
            
            const form = ref<Omit<Task, 'id' | 'projectId'> & { id?: number, projectId?: number | null }>({ 
              title: '',
              description: '',
              dueDate: '',
              priority: 'MEDIUM' as TaskPriority,
              status: 'TODO' as TaskStatus,
            })
            
            watch(() => props.show, (newShowVal) => {
              if (newShowVal) {
                if (props.task) {
                  // 編集モード: props.task の値でフォームを埋める
                  form.value = { ...props.task, dueDate: props.task.dueDate || '' };
                } else {
                  // 新規作成モード: フォームをリセット
                  form.value = {
                    title: '',
                    description: '',
                    dueDate: '',
                    priority: 'MEDIUM' as TaskPriority,
                    status: 'TODO' as TaskStatus,
                    projectId: props.projectId
                  };
                }
              }
            });
const close = () => {
  emit('close')
}

const save = () => {
  emit('save', form.value)
}
</script>