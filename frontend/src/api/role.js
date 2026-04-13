import request from '@/utils/request'

export const getRoleList = () => request.get('/system/role/list')
export const getRolePage = (params) => request.get('/system/role/page', { params })
export const getRoleById = (id) => request.get(`/system/role/${id}`)
export const createRole = (data) => request.post('/system/role', data)
export const updateRole = (data) => request.put(`/system/role/${data.id}`, data)
export const deleteRole = (id) => request.delete(`/system/role/${id}`)
export const assignRoleMenus = (id, menuIds) => request.put(`/system/role/${id}/menus`, menuIds)
export const getRoleMenuIds = (id) => request.get(`/system/role/${id}/menuIds`)
