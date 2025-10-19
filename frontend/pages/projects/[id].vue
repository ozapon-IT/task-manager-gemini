<template>
  <div v-if="project">
    <NuxtLink to="/projects" class="text-blue-500 hover:underline mb-4 inline-block">&larr; Back to Projects</NuxtLink>

    <h1 class="text-3xl font-bold">{{ project.title }}</h1>
    <p class="text-gray-500 dark:text-gray-400 mt-2">{{ project.description }}</p>

    <div class="mt-4 flex items-center space-x-4">
      <span :class="getStatusClass(project.status)" class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full">{{ project.status }}</span>
      <span>Due Date: {{ project.dueDate }}</span>
    </div>

    <!-- Tasks -->
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
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Priority</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
              <tr v-for="task in tasks" :key="task.id">
                <td class="px-6 py-4 whitespace-nowrap">{{ task.title }}</td>
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
                    <component :is="task.status.toUpperCase() === 'DONE' ? CheckCircle2Icon : CircleIcon" class="h-5 w-5" />
                  </button>
                  <button @click="showEditModal = true; selectedTask = task" class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2">
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

    <!-- Create/Edit Task Modal -->
    <EditTaskModal
      :show="showCreateModal || showEditModal"
      :task="selectedTask"
      :projectId="project.id"
      @close="showCreateModal = false; showEditModal = false; selectedTask = null"
      @save="handleSave"
    />
  </div>

  <div v-else>
    <p>Project not found.</p>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
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
const tasks = computed(() => {
  const statusOrder: Record<string, number> = {
    'IN_PROGRESS': 3,
    'TODO': 2,
    'DONE': 1,
  };

  const tasksFromStore = taskStore.getTasksByProjectId(projectId);

  return [...tasksFromStore].sort((a, b) => {
    // 1. ステータスによる比較
    const statusA = a.status.toUpperCase();
    const statusB = b.status.toUpperCase();
    const priorityA = statusOrder[statusA] ?? 0;
    const priorityB = statusOrder[statusB] ?? 0;

    if (priorityA !== priorityB) {
      return priorityB - priorityA; // 降順
    }

    // 2. dueDate による比較 (昇順)
    if (!a.dueDate) return 1;
    if (!b.dueDate) return -1;
    return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
  });
});

onMounted(async () => {
  if (projectStore.projects.length === 0) {
    await projectStore.fetchProjects()
  }
  // ストアにタスクがなければ、このプロジェクトのタスクを取得
  if (taskStore.getTasksByProjectId(projectId).length === 0) {
    await taskStore.fetchTasksByProjectId(projectId)
  }
})

const showCreateModal = ref(false)
const showEditModal = ref(false)
const selectedTask = ref<Task | null>(null)

const handleSave = async (task: Task) => {
  if (task.id) {
    await taskStore.updateTask(task)
  } else {
    await taskStore.createTask(projectId, task)
  }
  showCreateModal.value = false
  showEditModal.value = false
  selectedTask.value = null
}

const deleteTask = async (task: Task) => {
  if (confirm('Are you sure you want to delete this task?')) {
    await taskStore.deleteTask(task)
  }
}

const toggleTaskStatus = async (task: Task) => {
  await taskStore.toggleTaskStatus(task)
}

const getStatusClass = (status: ProjectStatus | TaskStatus) => {
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

const getPriorityClass = (priority: TaskPriority) => {
  switch (priority) {
    case 'HIGH': return 'bg-orange-200 text-orange-800 dark:bg-orange-900 dark:text-orange-200'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200'
    case 'LOW': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}
</script>
