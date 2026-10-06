import { defineStore } from "pinia";

export type Role = "User" | "Manager";

export interface User {
  name: string;
  email: string;
  role: Role;
}

const STORAGE_KEY = "user";

const USERS: User[] = [
  { name: "Adam", role: "Manager", email: "adam@glackit.be" },
  { name: "Tom", role: "Manager", email: "tom@glackit.be" },
  { name: "Jared", role: "Manager", email: "jared@glackit.be" },
  { name: "Stef", role: "User", email: "stef@gmail.com" },
  { name: "Jolie", role: "User", email: "jolie@gmail.com" },
  { name: "Samip", role: "Manager", email: "samip@glackit.be" },
  { name: "Annie", role: "Manager", email: "annie@glackit.be" },
  { name: "Laïs", role: "User", email: "lais@glackit.be" },
  { name: "Mouad", role: "User", email: "mouad@glackit.be" },
  { name: "Angelo", role: "User", email: "angelo@glackit.be" },
  { name: "Zakaria", role: "User", email: "zakaria@glackit.be" },
];

const isUser = (value: unknown): value is User => {
  if (!value || typeof value !== "object") return false;
  const candidate = value as Partial<User>;
  return (
    typeof candidate.name === "string" &&
    typeof candidate.email === "string" &&
    (candidate.role === "User" || candidate.role === "Manager")
  );
};

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
      const user = this.users.find((u) => u.name.toLowerCase() === name.toLowerCase());
      if (!user) return;

      this.currentUser = user;
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(user));
    },

    logout() {
      this.currentUser = null;
      sessionStorage.removeItem(STORAGE_KEY);
    },

    loadFromStorage() {
      const stored = sessionStorage.getItem(STORAGE_KEY);
      if (!stored) return;

      try {
        const parsed: unknown = JSON.parse(stored);
        if (isUser(parsed)) {
          this.currentUser = parsed;
          return;
        }
      } catch {
        sessionStorage.removeItem(STORAGE_KEY);
        return;
      }
      sessionStorage.removeItem(STORAGE_KEY);
    },
  },
});
