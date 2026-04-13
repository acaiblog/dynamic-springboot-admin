import request from '@/utils/request'

export const getGroupList = () => request.get('/system/group/list')
export const getGroupPage = (params) => request.get('/system/group/page', { params })
export const getGroupById = (id) => request.get(`/system/group/${id}`)
export const createGroup = (data) => request.post('/system/group', data)
export const updateGroup = (data) => request.put(`/system/group/${data.id}`, data)
export const deleteGroup = (id) => request.delete(`/system/group/${id}`)
export const assignGroupRoles = (id, roleIds) => request.put(`/system/group/${id}/roles`, roleIds)
export const assignGroupUsers = (id, userIds) => request.put(`/system/group/${id}/users`, userIds)
export const getGroupRoleIds = (id) => request.get(`/system/group/${id}/roleIds`)
export const getGroupUserIds = (id) => request.get(`/system/group/${id}/userIds`)
