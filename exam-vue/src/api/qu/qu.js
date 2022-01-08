import { post, upload, download } from '@/utils/request'

/**
 * 题库详情
 * @param data
 */
export function fetchDetail(id) {
  return post('/exam/api/qu/qu/detail', { id: id })
}

/**
 * 保存题库
 * @param data
 */
export function saveData(data) {
  return post('/exam/api/qu/qu/save', data)
}

/**
 * office文件分析
 * @param data
 */
export function officeAnalyze(data) {
  return post('/exam/api/qu/qu/office/analyse', data)
}

/**
 * office题 答案查询
 * @param data
 */
export function fetchOfficeAnswer(data) {
  return post('/exam/api/qu/qu/office/answer/detail', data)
}

export function readAnswer(data) {
  return post('/exam/api/qu/qu/office/read-answer', data)
}

/**
 * 导出
 * @param data
 */
export function exportExcel(data) {
  return download('/exam/api/qu/qu/export', data, '导出的数据.xlsx')
}

/**
 * 导入模板
 * @param data
 */
export function importTemplate() {
  return download('/exam/api/qu/qu/import/template', {}, 'qu-import-template.xlsx')
}

/**
 * 导入
 * @param data
 */
export function importExcel(file) {
  return upload('/exam/api/qu/qu/import', file)
}

