import { defineStore } from 'pinia';

export const useProjectStore = defineStore('project', {
  state: () => ({
    projects: [],
  }),
  actions: {
    async fetchProjects() {
      // TODO: Implement API call
    },
  },
});
