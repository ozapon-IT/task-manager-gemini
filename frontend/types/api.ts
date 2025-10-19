export type Project = {
  id: number
  title: string
  description: string
  dueDate: string
  status: ProjectStatus
  tasks?: Task[]
  createdAt?: string
  updatedAt?: string
}

export type Task = {
  id: number
  projectId?: number // Note: Backend sends the whole project object, but stores might need the ID.
  project?: Project
  title: string
  description: string
  dueDate: string
  priority: TaskPriority
  status: TaskStatus
  createdAt?: string
  updatedAt?: string
}

export type ProjectStatus = 'DRAFT' | 'ACTIVE' | 'COMPLETED'

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE'

export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH'