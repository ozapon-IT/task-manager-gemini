<template>
  <div>
    <h1 class="text-3xl font-bold mb-8">Dashboard</h1>

    <!-- Active Projects -->
    <div class="mb-8">
      <h2 class="text-2xl font-bold mb-4">Active Projects</h2>
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
              <tr v-for="project in activeProjects" :key="project.id">
                <td class="px-6 py-4 whitespace-nowrap">
                  <NuxtLink :to="`/projects/${project.id}`" class="text-blue-600 hover:underline">
                    {{ project.title }}
                  </NuxtLink>
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

    <!-- Tasks Overview -->
    <div>
      <h2 class="text-2xl font-bold mb-4">Tasks Overview</h2>
      <div class="bg-gray-100 dark:bg-gray-800 rounded-lg">
        <div class="overflow-x-auto">
          <table class="min-w-full">
            <thead class="border-b border-gray-200 dark:border-gray-700">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Project</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              <tr v-for="task in sortedTasks" :key="task.id">
                <td class="px-6 py-4 whitespace-nowrap">{{ task.title }}</td>
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
                  <button @click="toggleTaskStatus(task)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700">
                    <component :is="task.status === 'done' ? CheckCircle2Icon : CircleIcon" class="h-5 w-5" />
                  </button>
                  <button @click="openTaskEditModal(task)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
                    <PencilIcon class="h-5 w-5" />
                  </button>
                  <button @click="deleteTask(task)" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
                    <Trash2Icon class="h-5 w-5" />
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- Modals -->
    <EditTaskModal :show="isTaskEditModalOpen" :task="selectedTask" @close="closeTaskEditModal" @save="saveTask" />
    <EditProjectModal :show="isProjectEditModalOpen" :project="selectedProject" @close="closeProjectEditModal" @save="saveProject" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useProjectStore } from '~/store/project'
import { useTaskStore } from '~/store/tasks'
import { PencilIcon, Trash2Icon, CircleIcon, CheckCircle2Icon } from 'lucide-vue-next'
import type { Task, Project, TaskPriority, TaskStatus, ProjectStatus } from '~/types/api'
import EditTaskModal from '~/components/modal/EditTask.vue'
import EditProjectModal from '~/components/modal/EditProject.vue'

// Stores
const projectStore = useProjectStore()
const taskStore = useTaskStore()
const { projects } = storeToRefs(projectStore)
const { tasks } = storeToRefs(taskStore)

// Modal states
const isTaskEditModalOpen = ref(false)
const isProjectEditModalOpen = ref(false)
const selectedTask = ref<Task | null>(null)
const selectedProject = ref<Project | null>(null)

// Lifecycle
onMounted(async () => {
  await projectStore.fetchProjects()
  const taskPromises = projects.value.map(p => taskStore.fetchTasksByProjectId(p.id))
  await Promise.all(taskPromises)
})

// Computed
const activeProjects = computed(() =>
  projects.value
    .filter(p => p.status.toUpperCase() === 'ACTIVE')
    .sort((a, b) => {
      if (!a.dueDate) return 1;
      if (!b.dueDate) return -1;
      return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
    })
)

const sortedTasks = computed(() => {
  const now = new Date()
  const threeDaysLater = new Date()
  threeDaysLater.setDate(threeDaysLater.getDate() + 3)

  const isDueIn3Days = (dueDate: string) => {
    if (!dueDate) return false
    const date = new Date(dueDate)
    return date.getTime() <= threeDaysLater.getTime() && date.getTime() >= now.getTime()
  }

  const priorityOrder: Record<TaskPriority, number> = { HIGH: 3, MEDIUM: 2, LOW: 1 }
  
  // 'DONE' ステータスのタスクを除外する
  const filteredTasks = tasks.value ? tasks.value.filter(task => task.status.toUpperCase() !== 'DONE') : []

  return filteredTasks.sort((a, b) => {
    const aDueSoon = isDueIn3Days(a.dueDate)
    const bDueSoon = isDueIn3Days(b.dueDate)
    if (aDueSoon !== bDueSoon) return aDueSoon ? -1 : 1
    
    const aDate = a.dueDate ? new Date(a.dueDate).getTime() : 0
    const bDate = b.dueDate ? new Date(b.dueDate).getTime() : 0
    if (aDate !== bDate) return aDate - bDate

    return (priorityOrder[b.priority] ?? 0) - (priorityOrder[a.priority] ?? 0)
  })
})

// Modal functions
const openTaskEditModal = (task: Task) => {
  selectedTask.value = { ...task }
  isTaskEditModalOpen.value = true
}

const closeTaskEditModal = () => {
  isTaskEditModalOpen.value = false
  selectedTask.value = null
}

const openProjectEditModal = (project: Project) => {
  selectedProject.value = { ...project }
  isProjectEditModalOpen.value = true
}

const closeProjectEditModal = () => {
  isProjectEditModalOpen.value = false
  selectedProject.value = null
}

// CRUD operations
const saveTask = async (task: Task) => {
  if (task.id) {
    await taskStore.updateTask(task)
  } else {
    // Assuming createTask requires projectId
    if (task.projectId) {
      await taskStore.createTask(task.projectId, task)
    }
  }
  closeTaskEditModal()
}

const deleteTask = async (task: Task) => {
  if (confirm('Are you sure you want to delete this task?')) {
    await taskStore.deleteTask(task)
  }
}

const toggleTaskStatus = async (task: Task) => {
  await taskStore.toggleTaskStatus(task)
}

const saveProject = async (project: Project) => {
  if (project.id) {
    await projectStore.updateProject(project)
  } else {
    await projectStore.createProject(project)
  }
  closeProjectEditModal()
}

const handleProjectDelete = async (projectId: number) => {
  if (confirm('Are you sure you want to delete this project?')) {
    await projectStore.deleteProject(projectId)
  }
}

// Utilities
const getProjectName = (projectId: number) => {
  const project = projectStore.getProjectById(projectId)
  return project ? project.title : 'N/A'
}

const getPriorityClass = (priority: TaskPriority) => {
  switch (priority) {
    case 'HIGH': return 'bg-orange-200 text-orange-800 dark:bg-orange-900 dark:text-orange-200'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    case 'LOW': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}

const getStatusClass = (status: TaskStatus | ProjectStatus) => {
  switch (status) {
    case 'DRAFT':
    case 'TODO':
      return 'bg-gray-200 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
    case 'ACTIVE':
    case 'IN_PROGRESS':
      return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
    case 'COMPLETED':
    case 'DONE':
      return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default:
      return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}
</script>