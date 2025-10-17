<template>
  <div>
    <h1 class="text-3xl font-bold mb-8">Dashboard</h1>

    <!-- In-Progress Projects -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold mb-4">In-Progress Projects</h2>
      <div class="bg-gray-100 dark:bg-gray-800 rounded-lg">
        <div class="overflow-x-auto">
          <table class="min-w-full">
            <thead class="border-b border-gray-200 dark:border-gray-700">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              <tr v-for="project in inProgressProjects" :key="project.id">
                <td class="px-6 py-4 whitespace-nowrap">
                  <NuxtLink :to="`/projects/${project.id}`" class="text-blue-600 hover:underline">{{ project.name }}</NuxtLink>
                </td>
                <td class="px-6 py-4 whitespace-normal">{{ project.description }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ project.dueDate }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button @click="openProjectEditModal(project)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700">
                    <PencilIcon class="h-5 w-5" />
                  </button>
                  <button @click="handleProjectDelete(project.id)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
                    <Trash2Icon class="h-5 w-5" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Tasks -->
    <div>
      <h2 class="text-2xl font-bold mb-4">Tasks Overview</h2>
      <div class="bg-gray-100 dark:bg-gray-800 rounded-lg">
        <div class="overflow-x-auto">
          <table class="min-w-full">
            <thead class="border-b border-gray-200 dark:border-gray-700">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Task</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Project</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              <tr v-for="task in sortedTasks" :key="task.id">
                <td class="px-6 py-4 whitespace-nowrap">{{ task.name }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <NuxtLink :to="`/projects/${task.projectId}`" class="text-blue-600 hover:underline">
                    {{ getProjectName(task.projectId) }}
                  </NuxtLink>
                </td>
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
                  <button @click="openTaskEditModal(task)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
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
    <EditTaskModal :show="isTaskEditModalOpen" :task="selectedTask" @close="closeTaskEditModal" @save="saveTask" />
    <EditProjectModal :show="isProjectEditModalOpen" :project="selectedProject" @close="closeProjectEditModal" @save="saveProject" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useProjectStore } from '~/store/project'
import { useTaskStore } from '~/store/tasks'
import { PencilIcon, Trash2Icon, CircleIcon, CheckCircle2Icon } from 'lucide-vue-next'
import type { Task, Project, TaskPriority, TaskStatus, ProjectStatus } from '~/types/api'
import EditTaskModal from '~/components/modal/EditTask.vue'
import EditProjectModal from '~/components/modal/EditProject.vue'

// Project Store
const projectStore = useProjectStore()
const { updateProject, deleteProject: storeDeleteProject } = projectStore

// Task Store
const taskStore = useTaskStore()
const { deleteTask, toggleTaskStatus, updateTask } = taskStore

// Project Modal State
const isProjectEditModalOpen = ref(false)
const selectedProject = ref<Project | null>(null)

const openProjectEditModal = (project: Project) => {
  selectedProject.value = project
  isProjectEditModalOpen.value = true
}

const closeProjectEditModal = () => {
  isProjectEditModalOpen.value = false
  selectedProject.value = null
}

const saveProject = (project: Project) => {
  updateProject(project)
  closeProjectEditModal()
}

const handleProjectDelete = (id: number) => {
  if (confirm('Are you sure you want to delete this project?')) {
    storeDeleteProject(id)
  }
}

// Task Modal State
const isTaskEditModalOpen = ref(false)
const selectedTask = ref<Task | null>(null)

const openTaskEditModal = (task: Task) => {
  selectedTask.value = task
  isTaskEditModalOpen.value = true
}

const closeTaskEditModal = () => {
  isTaskEditModalOpen.value = false
  selectedTask.value = null
}

const saveTask = (task: Task) => {
  updateTask(task)
  closeTaskEditModal()
}

// Computed properties for data
const inProgressProjects = computed(() =>
  projectStore.projects.filter(p => p.status === 'in_progress' || p.status === 'active')
)

const priorityOrder: Record<TaskPriority, number> = {
  critical: 4,
  high: 3,
  medium: 2,
  low: 1,
}

const sortedTasks = computed(() => {
  const now = new Date()
  const threeDaysLater = new Date()
  threeDaysLater.setDate(threeDaysLater.getDate() + 3)

  const priorityOrder: Record<TaskPriority, number> = {
    critical: 4,
    high: 3,
    medium: 2,
    low: 1,
  }

  // 期日が3日以内かを判定
  const isDueIn3Days = (dueDate: string) => {
    const date = new Date(dueDate)
    return date.getTime() <= threeDaysLater.getTime() && date.getTime() >= now.getTime()
  }

  // リアクティブな tasks 配列を取り出し
  const tasks = taskStore.tasks ? [...taskStore.tasks] : []

  return tasks.sort((a, b) => {
    const aDueSoon = isDueIn3Days(a.dueDate)
    const bDueSoon = isDueIn3Days(b.dueDate)

    // ① 期日が3日以内のタスクを最優先
    if (aDueSoon !== bDueSoon) {
      return aDueSoon ? -1 : 1
    }

    // ② 期日が近い順（昇順）
    const aDate = new Date(a.dueDate).getTime()
    const bDate = new Date(b.dueDate).getTime()
    if (aDate !== bDate) {
      return aDate - bDate
    }

    // ③ 優先度が高い順（critical > high > medium > low）
    const aPriority = priorityOrder[a.priority] ?? 0
    const bPriority = priorityOrder[b.priority] ?? 0
    return bPriority - aPriority
  })
})

const getProjectName = (projectId: number) => {
  const project = projectStore.getProjectById(projectId)
  return project ? project.name : 'N/A'
}

// Style utility functions
const getPriorityClass = (priority: TaskPriority) => {
  switch (priority) {
    case 'critical': return 'bg-red-200 text-red-800 dark:bg-red-900 dark:text-red-200'
    case 'high': return 'bg-orange-200 text-orange-800 dark:bg-orange-900 dark:text-orange-200'
    case 'medium': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    case 'low': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}

const getStatusClass = (status: TaskStatus | ProjectStatus) => {
  switch (status) {
    case 'todo': return 'bg-gray-200 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
    case 'in_progress':
    case 'active':
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
    case 'done': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}
</script>