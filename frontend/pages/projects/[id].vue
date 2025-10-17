<template>
  <div>
    <div v-if="project">
      <NuxtLink to="/projects" class="text-blue-500 hover:underline mb-4 inline-block">&larr; Back to Projects</NuxtLink>
      <h1 class="text-3xl font-bold">{{ project.name }}</h1>
      <p class="text-gray-500 dark:text-gray-400 mt-2">{{ project.description }}</p>
      <div class="mt-4 flex items-center space-x-4">
        <span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full" :class="getStatusClass(project.status)">{{ project.status }}</span>
        <span>Due: {{ project.dueDate }}</span>
      </div>

      <div class="mt-12">
        <div class="flex justify-between items-center mb-4">
            <h2 class="text-2xl font-bold">Tasks</h2>
            <button @click="showCreateModal = true" class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg flex items-center">
                <PlusIcon class="h-5 w-5 mr-2" />
                New Task
            </button>
        </div>
        <div class="bg-gray-100 dark:bg-gray-800 rounded-lg">
          <div class="overflow-x-auto">
            <table class="min-w-full">
              <thead class="border-b border-gray-200 dark:border-gray-700">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Task</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
                <tr v-for="task in tasks" :key="task.id">
                  <td class="px-6 py-4 whitespace-nowrap">{{ task.name }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="getPriorityClass(task.priority)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                      {{ task.priority }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap">{{ task.dueDate }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="getStatusClass(task.status)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">
                      {{ task.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button @click="toggleTaskStatus(task.id)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700">
                        <component :is="task.status === 'done' ? CheckCircle2Icon : CircleIcon" class="h-5 w-5" />
                    </button>
                    <button @click="showEditModal = true; selectedTask = task" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
                      <PencilIcon class="h-5 w-5" />
                    </button>
                    <button @click="deleteTask(task.id)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
                      <Trash2Icon class="h-5 w-5" />
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <p>Project not found.</p>
    </div>

    <!-- Create/Edit Task Modal -->
    <EditTaskModal
      :show="showCreateModal || showEditModal"
      :task="selectedTask"
      :projectId="project?.id"
      @close="showCreateModal = false; showEditModal = false; selectedTask = null"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useProjectStore } from '~/store/project'
import { useTaskStore } from '~/store/tasks'
import { PlusIcon, PencilIcon, Trash2Icon, CircleIcon, CheckCircle2Icon } from 'lucide-vue-next'
import EditTaskModal from '~/components/modal/EditTask.vue'
import type { Project, Task, ProjectStatus, TaskStatus, TaskPriority } from '~/types/api'

const route = useRoute()
const projectStore = useProjectStore()
const taskStore = useTaskStore()

const projectId = Number(route.params.id)
const project = computed(() => projectStore.getProjectById(projectId))
const tasks = computed(() => taskStore.getTasksByProjectId(projectId))

const { addTask, updateTask, deleteTask, toggleTaskStatus } = taskStore

const showCreateModal = ref(false)
const showEditModal = ref(false)
const selectedTask = ref<Task | null>(null)

const handleSave = (task: Task) => {
  if (task.id) {
    updateTask(task)
  } else {
    addTask({ ...task, projectId })
  }
  showCreateModal.value = false
  showEditModal.value = false
  selectedTask.value = null
}

const getStatusClass = (status: ProjectStatus | TaskStatus) => {
  switch (status) {
    case 'todo': return 'bg-gray-200 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
    case 'in_progress': return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
    case 'done': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
  }
}

const getPriorityClass = (priority: TaskPriority) => {
  switch (priority) {
    case 'critical': return 'bg-red-200 text-red-800 dark:bg-red-900 dark:text-red-200'
    case 'high': return 'bg-orange-200 text-orange-800 dark:bg-orange-900 dark:text-orange-200'
    case 'medium': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    case 'low': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
  }
}
</script>