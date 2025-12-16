import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000
})

service.interceptors.request.use(
  config => {
    const userStore = useUserStore()
    if (userStore.token) {
      config.headers['Authorization'] = `Bearer ${userStore.token}`
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    // Assuming 200 is success, adjusting based on ApiResponse
    // If backend returns { code: 200, message: "OK", data: ... }
    if (res.code && res.code !== 200) {
      ElMessage.error(res.message || 'Error')
      if (res.code === 401) {
        const userStore = useUserStore()
        userStore.logout()
        location.reload()
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data // Return the data directly
  },
  error => {
    const { response } = error
    if (response) {
      switch (response.status) {
        case 401:
          // const userStore = useUserStore()
          // userStore.logout()
          // location.reload()
          break
        case 403:
          ElMessage.error('没有权限')
          break
        case 404:
          ElMessage.error('资源不存在')
          break
        case 500:
          ElMessage.error('服务器错误')
          break
        default:
          ElMessage.error(response.data?.message || '未知错误')
      }
    } else {
      ElMessage.error('网络错误')
    }
    return Promise.reject(error)
  }
)

export default service
