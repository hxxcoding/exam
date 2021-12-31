import { post } from '@/utils/request'

export function deleteFile(url, fileUrl) {
  return post(url, { 'url': fileUrl })
}

export function uploadFile(file) {
  return post('/common/api/file/upload', file)
}
