export type Project = {
  id: number
  name: string
  description: string
  dueDate: string
  status: ProjectStatus
}

export type Task = {
  id: number
  projectId: number
  name: string
  description: string
  dueDate: string
  priority: TaskPriority
  status: TaskStatus
}

export type ProjectStatus = 'todo' | 'in_progress' | 'done'

export type TaskStatus = 'todo' | 'in_progress' | 'done'

export type TaskPriority = 'low' | 'medium' | 'high'
