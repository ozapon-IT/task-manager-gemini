<template>
  <div>
    <div class="flex justify-between items-center mb-8">
      <h1 class="text-3xl font-bold">Projects</h1>
      <button
        @click="showCreateModal = true"
        class="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-lg flex items-center"
      >
        <PlusIcon class="h-5 w-5 mr-2" />
        New Project
      </button>
    </div>

    <!-- Projects Table -->
    <div class="bg-gray-100 dark:bg-gray-800 rounded-lg">
      <div class="overflow-x-auto">
        <table class="min-w-full">
          <thead class="border-b border-gray-200 dark:border-gray-700">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Description</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
              <th class="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200 dark:divide-gray-700">
            <tr v-for="project in sortedProjects" :key="project.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <NuxtLink
                  :to="`/projects/${project.id}`"
                  class="text-blue-500 hover:underline"
                >
                  {{ project.title }}
                </NuxtLink>
              </td>
              <td class="px-6 py-4 whitespace-normal">{{ project.description }}</td>
              <td class="px-6 py-4 whitespace-nowrap">{{ project.dueDate }}</td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="getStatusClass(project.status)"
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full"
                >
                  {{ project.status }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                <button
                  @click="showEditModal = true; selectedProject = project"
                  class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700"
                >
                  <PencilIcon class="h-5 w-5" />
                </button>
                <button
                  @click="handleDelete(project.id)"
                  class="p-2 rounded-full hover:bg-gray-200 dark:hover:bg-gray-700 ml-2"
                >
                  <Trash2Icon class="h-5 w-5" />
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Create/Edit Modal -->
    <EditProjectModal
      :show="showCreateModal || showEditModal"
      :project="selectedProject"
      @close="showCreateModal = false; showEditModal = false; selectedProject = null"
      @save="handleSave"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue' // computed をインポート
import { storeToRefs } from 'pinia'
import { useProjectStore } from '~/store/project'
import { PlusIcon, PencilIcon, Trash2Icon } from 'lucide-vue-next'
import EditProjectModal from '~/components/modal/EditProject.vue'
import type { Project, ProjectStatus } from '~/types/api'

// Project Store
const projectStore = useProjectStore()
const { projects } = storeToRefs(projectStore)
const { createProject, updateProject, deleteProject, fetchProjects } = projectStore

const sortedProjects = computed(() => {
  const statusOrder: Record<string, number> = {
    'ACTIVE': 3,
    'DRAFT': 2,
    'COMPLETED': 1,
  };

  return [...projects.value].sort((a, b) => {
    // 1. ステータスによる比較 (ACTIVE > DRAFT > COMPLETED)
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

// Local state
const showCreateModal = ref(false)
const showEditModal = ref(false)
const selectedProject = ref<Project | null>(null)

// Lifecycle - Fetch projects from API
onMounted(async () => {
  await fetchProjects()
})

// Save handler
const handleSave = async (project: Project) => {
  if (project.id) {
    await updateProject(project)
  } else {
    await createProject(project)
  }
  showCreateModal.value = false
  showEditModal.value = false
  selectedProject.value = null
}

// Delete handler
const handleDelete = async (id: number) => {
  if (confirm('Are you sure you want to delete this project?')) {
    await deleteProject(id)
  }
}

// Status label style
const getStatusClass = (status: ProjectStatus) => {
  switch (status) {
    case 'DRAFT': return 'bg-gray-200 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
    case 'ACTIVE': return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-200'
    case 'COMPLETED': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200'
    default: return 'bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200'
  }
}
</script>
