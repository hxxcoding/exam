import { post } from '@/utils/request'

/**
 * 向在线考生发送通知
 * @param message
 * @param paperId
 */
export function sendMsg(message, paperId) {
  return post('/exam/api/paper/paper/send-msg', {
    paperId: paperId,
    message: message
  })
}
