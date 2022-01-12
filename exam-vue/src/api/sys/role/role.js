import { post } from '@/utils/request'

export function fetchList() {
  return post('/exam/api/sys/role/list', {})
}

export function updateRoleMenu(data) {
  return post('/exam/api/sys/role/menu/update', data)
}
