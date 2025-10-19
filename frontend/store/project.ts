import { defineStore } from 'pinia'
import type { Project } from '~/types/api'
import { useTaskStore } from './tasks' // useTaskStore をインポート

export const useProjectStore = defineStore('projects', {
  state: () => ({
    projects: [] as Project[],
    isLoading: false,
  }),

  getters: {
    getProjectById: (state) => (id: number) =>
      state.projects.find(p => p.id === id),
    getInProgressProjectsCount: (state) =>
      state.projects.filter(p => p.status === 'in_progress' || p.status === 'active').length,
  },

  actions: {
    // 📥 プロジェクト一覧取得
    async fetchProjects() {
      this.isLoading = true
      const { $api } = useNuxtApp()
      try {
        const res = await $api.get('/projects')

        // ✅ Spring Data Page対応
        if (res.data && Array.isArray(res.data.content)) {
          this.projects = res.data.content
        } else if (Array.isArray(res.data)) {
          this.projects = res.data
        } else {
          console.warn('Unexpected /api/projects response:', res)
          this.projects = []
        }

      } catch (error) {
        console.error('Failed to fetch projects:', error)
        this.projects = []
      } finally {
        this.isLoading = false
      }
    },

    // ➕ プロジェクト作成
    async createProject(project: Omit<Project, 'id'>) {
      const { $api } = useNuxtApp()
      try {
        const res = await $api.post<Project>('/projects', project)
        this.projects.push(res.data)
      } catch (error) {
        console.error('Failed to create project:', error)
      }
    },

    // 🔄 プロジェクト更新
    async updateProject(updatedProject: Project) {
      const { $api } = useNuxtApp()
      try {
        const res = await $api.put<Project>(`/projects/${updatedProject.id}`, updatedProject)
        const index = this.projects.findIndex(project => project.id === res.data.id)
        if (index !== -1) {
          this.projects[index] = res.data
        }
      } catch (error) {
        console.error('Failed to update project:', error)
      }
    },

    // ❌ プロジェクト削除
    async deleteProject(id: number) {
      const { $api } = useNuxtApp()
      const taskStore = useTaskStore()

      try {
        await $api.delete(`/projects/${id}`)
        this.projects = this.projects.filter(project => project.id !== id)
        taskStore.removeTasksByProjectId(id) // 関連タスクをストアから削除
      } catch (error) {
        console.error('Failed to delete project:', error)
      }
    },
  },
})
