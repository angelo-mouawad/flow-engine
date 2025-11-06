import { defineStore } from 'pinia';

export const useRoleStore = defineStore('role', {
  state: () => ({
    role: 'User' as 'User' | 'Admin',
  }),
  actions: {
    setRole(newRole: 'User' | 'Admin') {
        this.role = newRole;
    }
  }
})