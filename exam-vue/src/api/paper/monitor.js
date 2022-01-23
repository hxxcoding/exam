import { post } from '@/utils/request'

/**
 * 向在线考生发送通知
 * @param userId
 * @param message
 */
export function sendMsg(message, userId) {
  return post('/exam/api/exam/exam/send-msg', {
    userId: userId,
    message: message
  })
}
