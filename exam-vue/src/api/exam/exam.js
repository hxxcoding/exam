import { post } from '@/utils/request'

/**
 * 考试详情
 * @param examId
 */
export function fetchDetail(examId) {
  return post('/exam/api/exam/exam/detail', { id: examId })
}

/**
 * 考试详情
 * @param examId
 */
export function fetchPreview(examId) {
  return post('/exam/api/exam/exam/online-preview', { id: examId })
}

/**
 * 保存题库
 * @param data
 */
export function saveData(data) {
  return post('/exam/api/exam/exam/save', data)
}

/**
 * 题库详情
 * @param data
 */
export function fetchList() {
  return post('/exam/api/exam/exam/paging', { current: 1, size: 100 })
}
