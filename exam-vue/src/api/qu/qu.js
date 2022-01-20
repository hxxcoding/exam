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
 * word文件段落分析
 * @param url
 */
export function wordParagraphsAnalyze(url) {
  return post('/exam/api/qu/qu/office/analyse/word', {
    url: url
  })
}

/**
 * ppt文件段落分析
 * @param url
 */
export function pptSlidesAnalyze(url) {
  return post('/exam/api/qu/qu/office/analyse/ppt', {
    url: url
  })
}

/**
 * 获取office题判分方法
 * @param quType
 */
export function fetchQuOfficeMethods(quType) {
  return post('/exam/api/qu/qu/office/method', {
    quType: quType
  })
}

/**
 * 读取Word格式
 * @param url
 * @param pos
 * @param method
 * @returns {Promise}
 */
export function readFormat(url, pos, method) {
  return post('/exam/api/qu/qu/office/read-format', {
    url: url,
    pos: pos,
    method: method
  })
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

