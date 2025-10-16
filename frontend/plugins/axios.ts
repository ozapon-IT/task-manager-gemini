import axios from 'axios';

export default defineNuxtPlugin((nuxtApp) => {
  const api = axios.create({
    baseURL: '/api',
  });

  return {
    provide: {
      api,
    },
  };
});
