import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getSystemTitle } from '@/api/config'

export const useConfigStore = defineStore('config', () => {
  const systemTitle = ref('后台管理系统')

  async function fetchTitle() {
    try {
      const res = await getSystemTitle()
      if (res.data) {
        systemTitle.value = res.data
        document.title = res.data
      }
    } catch (e) {
      // use default title
    }
  }

  function setTitle(title) {
    systemTitle.value = title
    document.title = title
  }

  return { systemTitle, fetchTitle, setTitle }
})
