import { defineStore } from 'pinia'
import type { Task } from '~/types/api'

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    tasks: [] as Task[],
    isLoading: false,
  }),

  getters: {
    getTasksByProjectId: (state) => (projectId: number) =>
      state.tasks.filter(task => task.projectId === projectId),
  },

  actions: {
    async fetchTasksByProjectId(projectId: number) {
      this.isLoading = true
      const { $api } = useNuxtApp()
      try {
        const res = await $api.get<Task[]>(`/projects/${projectId}/tasks`)
        if (Array.isArray(res.data)) {
          // APIレスポンスのタスクに projectId を付与する
          const tasksWithProjectId = res.data.map(task => ({ ...task, projectId }))

          // 既存のタスクを維持しつつ、新しいタスクを追加（重複を避ける）
          const existingTaskIds = new Set(this.tasks.map(t => t.id))
          const newTasks = tasksWithProjectId.filter(t => !existingTaskIds.has(t.id))
          this.tasks.push(...newTasks)
        }
      } catch (error) {
        console.error(`Failed to fetch tasks for project ${projectId}:`, error)
      } finally {
        this.isLoading = false
      }
    },

    // ➕ タスク作成
    async createTask(projectId: number, task: Omit<Task, 'id' | 'projectId'>) {
      const { $api } = useNuxtApp()
      try {
        const res = await $api.post<Task>(`/projects/${projectId}/tasks`, task)
        this.tasks.push({ ...res.data, projectId })
      } catch (error) {
        console.error('Failed to create task:', error)
      }
    },

    async updateTask(updatedTask: Task) {
      const { $api } = useNuxtApp()
      try {
        const res = await $api.put<Task>(
          `/projects/${updatedTask.projectId}/tasks/${updatedTask.id}`,
          updatedTask
        )
        const index = this.tasks.findIndex(task => task.id === res.data.id)
        if (index !== -1) {
          // 元の projectId を維持しつつ、レスポンスデータで更新する
          this.tasks[index] = { ...res.data, projectId: this.tasks[index].projectId }
        }
      } catch (error) {
        console.error('Failed to update task:', error)
      }
    },

    // ❌ タスク削除
    async deleteTask(task: Task) {
      const { $api } = useNuxtApp()
      try {
        await $api.delete(`/projects/${task.projectId}/tasks/${task.id}`)
        this.tasks = this.tasks.filter(t => t.id !== task.id)
      } catch (error) {
        console.error('Failed to delete task:', error)
      }
    },

    // 🔁 ステータス切替
    async toggleTaskStatus(task: Task) {
      // ステータスは大文字・小文字を区別せずに比較し、大文字のEnum値に設定する
      const newStatus = task.status.toUpperCase() === 'DONE' ? 'TODO' : 'DONE'
      const updatedTask = { ...task, status: newStatus }
      await this.updateTask(updatedTask)
    },

    // プロジェクトIDを指定してタスクをストアから削除
    removeTasksByProjectId(projectId: number) {
      this.tasks = this.tasks.filter(task => task.projectId !== projectId)
    },
  },
})
