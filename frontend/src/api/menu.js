import request from '@/utils/request'

export const getMenuTree = () => request.get('/system/menu/tree')
export const getRouters = () => request.get('/system/menu/routers')
export const getMenuById = (id) => request.get(`/system/menu/${id}`)
export const createMenu = (data) => request.post('/system/menu', data)
export const updateMenu = (data) => request.put('/system/menu', data)
export const deleteMenu = (id) => request.delete(`/system/menu/${id}`)
