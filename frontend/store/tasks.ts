import { defineStore } from 'pinia'
import type { Task, TaskStatus, TaskPriority } from '~/types/api'

export const useTaskStore = defineStore('tasks', {
  state: () => ({
    tasks: [
      { id: 1, projectId: 1, name: 'Design the dashboard', description: 'Create a modern and clean design for the dashboard.', dueDate: '2025-10-20', priority: 'high' as TaskPriority, status: 'in_progress' as TaskStatus },
      { id: 2, projectId: 1, name: 'Develop the API for projects', description: 'Implement the CRUD API for projects.', dueDate: '2025-10-22', priority: 'medium' as TaskPriority, status: 'todo' as TaskStatus },
      { id: 3, projectId: 2, name: 'Implement the task list', description: 'Create the task list component with sorting and filtering.', dueDate: '2025-10-25', priority: 'high' as TaskPriority, status: 'todo' as TaskStatus },
      { id: 4, projectId: 2, name: 'Add dark mode support', description: 'Implement dark mode for the entire application.', dueDate: '2025-10-18', priority: 'low' as TaskPriority, status: 'done' as TaskStatus },
    ] as Task[],
  }),
  getters: {
    getTasksByProjectId: (state) => (projectId: number) => {
      return state.tasks.filter(task => task.projectId === projectId)
    },
    getTaskById: (state) => (id: number) => {
      return state.tasks.find(task => task.id === id)
    },
    getHighPriorityTasks: (state) => {
        return state.tasks.filter(task => task.priority === 'high' && task.status !== 'done')
    },
    getTasksDueToday: (state) => {
        const today = new Date().toISOString().split('T')[0]
        return state.tasks.filter(task => task.dueDate === today)
    },
    getTasksDueIn3Days: (state) => {
        const today = new Date()
        const in3Days = new Date()
        in3Days.setDate(today.getDate() + 3)
        return state.tasks.filter(task => {
            const dueDate = new Date(task.dueDate)
            return dueDate >= today && dueDate <= in3Days
        })
    }
  },
  actions: {
    addTask(task: Omit<Task, 'id'>) {
      const newTask = { ...task, id: Math.max(...this.tasks.map(t => t.id)) + 1 } as Task
      this.tasks.push(newTask)
    },
    updateTask(updatedTask: Task) {
      const index = this.tasks.findIndex(task => task.id === updatedTask.id)
      if (index !== -1) {
        this.tasks[index] = updatedTask
      }
    },
    deleteTask(id: number) {
      this.tasks = this.tasks.filter(task => task.id !== id)
    },
    toggleTaskStatus(id: number) {
        const task = this.getTaskById(id)
        if (task) {
            task.status = task.status === 'done' ? 'todo' : 'done'
            this.updateTask(task)
        }
    }
  },
})
