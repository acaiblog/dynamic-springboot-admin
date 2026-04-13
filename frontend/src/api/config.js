import request from '@/utils/request'

export const getConfigList = () => request.get('/system/config/list')
export const getConfigById = (id) => request.get(`/system/config/${id}`)
export const getConfigByKey = (key) => request.get(`/system/config/key/${key}`)
export const createConfig = (data) => request.post('/system/config', data)
export const updateConfig = (id, data) => request.put(`/system/config/${id}`, data)
export const deleteConfig = (id) => request.delete(`/system/config/${id}`)
export const getSystemTitle = () => request.get('/system/config/title')
export const setSystemTitle = (title) => request.put('/system/config/title', { title })
export const getSystemIntro = () => request.get('/system/config/intro')
export const setSystemIntro = (intro) => request.put('/system/config/intro', { intro })
