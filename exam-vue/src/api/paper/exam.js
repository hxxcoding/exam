import { post } from '@/utils/request'

/**
 * 创建试卷
 * @param data
 */
export function createPaper(data) {
  return post('/exam/api/paper/paper/create-paper', data)
}

/**
 * 试卷详情
 * @param data
 */
export function paperDetail(data) {
  return post('/exam/api/paper/paper/paper-detail', data)
}

/**
 * 获取Office题得分点
 * @param paperId
 * @param quId
 * @returns {Promise}
 */
export function fetchQuOfficePoints(paperId, quId) {
  return post('/exam/api/paper/paper/paper-result/office/points', {
    paperId: paperId,
    quId: quId
  })
}

/**
 * 题目详情
 * @param data
 */
export function quDetail(data) {
  return post('/exam/api/paper/paper/qu-detail', data)
}

/**
 * 填充答案
 * @param data
 */
export function fillAnswer(data) {
  return post('/exam/api/paper/paper/fill-answer', data)
}

/**
 * 异步填充答案
 * @param data
 */
export function fillAnswerByAsync(data) {
  return post('/exam/api/paper/paper/fill-answer-by-async', data)
}

/**
 * 交卷
 * @param data
 */
export function handExam(data) {
  return post('/exam/api/paper/paper/hand-exam', data)
}

/**
 * 强制交卷
 * @param data
 */
export function handExamByForce(data) {
  return post('/exam/api/paper/paper/hand-exam-by-force', data)
}

/**
 * 退回交卷
 * @param data
 */
export function backExam(data) {
  return post('/exam/api/paper/paper/back-exam', data)
}

/**
 * 试卷详情
 * @param data
 */
export function paperResult(data) {
  return post('/exam/api/paper/paper/paper-result', data)
}
