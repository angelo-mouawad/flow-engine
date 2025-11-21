import { defineStore } from "pinia";

const STORAGE_KEY = "theme-mode";

type ThemeState = {
  isDarkMode: boolean;
};

const getInitialTheme = (): boolean => {
  if (typeof window === "undefined") {
    return false;
  }

  const stored = window.localStorage.getItem(STORAGE_KEY);
  if (stored !== null) return stored === "dark";
  if (window.matchMedia) {
    return window.matchMedia("(prefers-color-scheme: dark)").matches;
  }
  return false;
};

export const useThemeStore = defineStore("theme", {
  state: (): ThemeState => ({
    isDarkMode: getInitialTheme(),
  }),
  actions: {
    toggleTheme() {
      this.setDarkMode(!this.isDarkMode);
    },
    setDarkMode(value: boolean) {
      this.isDarkMode = value;
      if (typeof window !== "undefined") {
        window.localStorage.setItem(STORAGE_KEY, value ? "dark" : "light");
      }
    },
  },
});
