import { defineStore } from 'pinia'
import type { Project, ProjectStatus } from '~/types/api'

export const useProjectStore = defineStore('projects', {
  state: () => ({
    projects: [
      { id: 1, name: 'Task Manager App', description: 'A simple task manager application built with Nuxt 3 and Spring Boot.', dueDate: '2025-11-01', status: 'in_progress' as ProjectStatus },
      { id: 2, name: 'Portfolio Website', description: 'A personal portfolio website to showcase my projects.', dueDate: '2025-12-15', status: 'todo' as ProjectStatus },
    ] as Project[],
  }),
  getters: {
    getProjectById: (state) => (id: number) => {
      return state.projects.find(project => project.id === id)
    },
    getInProgressProjectsCount: (state) => {
        return state.projects.filter(p => p.status === 'in_progress').length
    }
  },
  actions: {
    addProject(project: Omit<Project, 'id'>) {
      const newProject = { ...project, id: Math.max(...this.projects.map(p => p.id)) + 1 } as Project
      this.projects.push(newProject)
    },
    updateProject(updatedProject: Project) {
      const index = this.projects.findIndex(project => project.id === updatedProject.id)
      if (index !== -1) {
        this.projects[index] = updatedProject
      }
    },
    deleteProject(id: number) {
      this.projects = this.projects.filter(project => project.id !== id)
    },
  },
})
