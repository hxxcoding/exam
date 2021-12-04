import { post } from '@/utils/request'

export function deleteFile(url, fileUrl) {
  return post(url, { 'url': fileUrl })
}
