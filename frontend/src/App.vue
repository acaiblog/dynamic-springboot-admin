<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useConfigStore } from '@/store/config'

const configStore = useConfigStore()

onMounted(() => {
  configStore.fetchTitle()
})
</script>
