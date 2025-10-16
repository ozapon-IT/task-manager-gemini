export interface Project {
  id: number;
  name: string;
  createdAt: string;
  updatedAt: string;
}

export interface Task {
  id: number;
  projectId: number;
  title: string;
  description: string;
  status: string;
  createdAt: string;
  updatedAt: string;
}
