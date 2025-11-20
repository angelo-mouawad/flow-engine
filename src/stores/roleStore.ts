import { defineStore } from 'pinia';

export const useRoleStore = defineStore('role', {
  state: () => ({
    role: sessionStorage.getItem('role') || 'User',
  }),
  actions: {
    setRole(newRole: string) {
      this.role = newRole;
      sessionStorage.setItem('role', newRole);
    }
  }
});