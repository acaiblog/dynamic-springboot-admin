import request from '@/utils/request'

export const getUserPage = (params) => request.get('/system/user/page', { params })
export const getUserById = (id) => request.get(`/system/user/${id}`)
export const createUser = (data) => request.post('/system/user', data)
export const updateUser = (data) => request.put('/system/user', data)
export const deleteUser = (id) => request.delete(`/system/user/${id}`)
export const updateUserStatus = (id, status) => request.put(`/system/user/${id}/status/${status}`)
export const resetPassword = (id, password) => request.put(`/system/user/${id}/resetPassword`, { password })
export const assignUserRoles = (id, roleIds) => request.put(`/system/user/${id}/roles`, roleIds)
