import { defineStore } from 'pinia'
import { ref } from 'vue'

export type ThemeName = 'light' | 'dark' | 'green'

const THEME_KEY = 'blog-theme'

export const useThemeStore = defineStore('theme', () => {
    const theme = ref<ThemeName>(
        (localStorage.getItem(THEME_KEY) as ThemeName) || 'light'
    )

    function applyTheme(t: ThemeName) {
        document.documentElement.setAttribute('data-theme', t)
    }

    function setTheme(t: ThemeName) {
        theme.value = t
        localStorage.setItem(THEME_KEY, t)
        applyTheme(t)
    }

    // store 初始化时立即应用主题
    applyTheme(theme.value)

    return { theme, setTheme }
})