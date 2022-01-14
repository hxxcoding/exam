import { post } from '@/utils/request'

export function fetchList() {
  return post('/exam/api/sys/role/list', {})
}

export function updateRoleMenu(roleId, menuIds) {
  return post('/exam/api/sys/role/menu/update', {
    id: roleId,
    menuIds: menuIds
  })
}
