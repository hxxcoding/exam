import axios from 'axios'

axios.defaults.baseURL = process.env.VUE_APP_BASE_API

const service = axios.create({
  timeout: 40000,
  headers: {
    'X-Requested-With': 'XMLHttpRequest',
    'Content-Type': 'application/json; charset=UTF-8'
  }
})
service.interceptors.request.use(
  config => {
    return config
  },
  error => {
    Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  response => {
    return response.data
  },
  // eslint-disable-next-line handle-callback-err
  error => {
  }
)
export default service
