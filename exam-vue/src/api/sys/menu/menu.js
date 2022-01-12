import { post } from '@/utils/request'

export function listMenu() {
  return post('/exam/api/sys/menu/list', {})
}

export function menuDetail(menuId) {
  return post('/exam/api/sys/menu/detail', {
    id: menuId
  })
}

export function saveMenu(data) {
  return post('/exam/api/sys/menu/save', data)
}

export function deleteMenu(menuId) {
  return post('/exam/api/sys/menu/delete', {
    id: menuId
  })
}

// 根据角色ID查询菜单下拉树结构
export function listMenuTreeByRole(roleId) {
  return post('/exam/api/sys/menu/list-tree-by-role', {
    id: roleId
  })
}
