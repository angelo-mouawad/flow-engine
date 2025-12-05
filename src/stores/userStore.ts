import { defineStore } from "pinia";

export type Role = "User" | "Manager";

export interface User {
  name: string;
  email: string;
  role: Role;
}

const USERS: User[] = [  
  { name: "Adam", role: "Manager", email: "adam@glackit.be" },
  { name: "Tom", role: "Manager", email: "tom@glackit.be" },
  { name: "Jared", role: "Manager", email: "Jared@glackit.be" },
  { name: "Stef", role: "User", email: "stef@gmail.com" },
  { name: "Jolie", role: "User", email: "jolie@gmail.com" },
  { name: "Samip", role: "Manager", email: "samip@glackit.be" },
  { name: "Annie", role: "Manager", email: "annie@glackit.be" },
];

export const useUserStore = defineStore("userStore", {
  state: () => ({
    currentUser: null as User | null,
    users: USERS,
  }),

  getters: {
    isManager: (state) => state.currentUser?.role === "Manager",
    isLoggedIn: (state) => !!state.currentUser,
  },

  actions: {
    login(name: string) {
      const user = this.users.find(u => u.name.toLowerCase() === name.toLowerCase());
      if (!user) return;

      this.currentUser = user;
      sessionStorage.setItem("user", JSON.stringify(user));
    },

    logout() {
      this.currentUser = null;
      sessionStorage.removeItem("user");
    },

    loadFromStorage() {
      const stored = sessionStorage.getItem("user");
      if (stored) {
        this.currentUser = JSON.parse(stored);
      }
    },
  }
})
