import { download, post, upload } from '@/utils/request'

export function fetchRouters() {
  return post('/exam/api/sys/user/router', {})
}

export function updateData(data) {
  return post('/exam/api/sys/user/update', data)
}

export function saveData(data) {
  return post('/exam/api/sys/user/save', data)
}

export function userReg(data) {
  return post('/exam/api/sys/user/reg', data)
}

/**
 * 导入模板
 * @param data
 */
export function importTemplate() {
  return download('/exam/api/sys/user/import/template', {}, 'user-import-template.xlsx')
}

/**
 * 导入
 * @param data
 */
export function importExcel(file) {
  return upload('/exam/api/sys/user/import', file)
}
