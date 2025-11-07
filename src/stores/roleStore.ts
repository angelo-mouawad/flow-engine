import { defineStore } from 'pinia';

export const useRoleStore = defineStore('role', {
  state: () => ({
    role: 'User' as 'User' | 'Manager',
  }),
  actions: {
    setRole(newRole: 'User' | 'Manager') {
        this.role = newRole;
    }
  }
})