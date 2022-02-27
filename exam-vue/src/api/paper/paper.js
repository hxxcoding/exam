import { download, post } from '@/utils/request'

/**
 * 试卷列表
 * @param userId
 * @param examId
 */
export function listPaper(userId, examId) {
  return post('/exam/api/paper/paper/paging', { current: 1, size: 5, params: { userId: userId, examId: examId }})
}

/**
 * 导出成绩
 * @param data
 */
export function exportExcel(data) {
  return download('/exam/api/paper/paper/export', data, '导出成绩-' + new Date().getTime() + '.xlsx')
}

export function exportPaper(ids) {
  return post('/exam/api/paper/paper/export/paper', { ids: ids })
}
