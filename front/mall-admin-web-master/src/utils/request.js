import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '../store'
import { getToken, getRoleByRequestUrl } from '@/utils/auth'

const service = axios.create({
  baseURL: '/api',
  timeout: 5000,
  withCredentials: false
})

service.interceptors.request.use(
  config => {
    // 按接口前缀自动选择对应身份 token，支持 admin/user 同时在线
    const token = getToken(config.url)
    if (token) {
      config.headers.Authorization = token
    }
    return config
  },
  error => Promise.reject(error)
)

service.interceptors.response.use(
  response => {
    const res = response.data
    const silent = response.config && response.config.silent

    if (res.code !== 1) {
      if (!silent) {
        const message = res.msg || res.message
        if (message) {
          Message({ message, type: 'error', duration: 3000 })
        }
      }

      if (res.code === 401 && !silent) {
        const failedRole = getRoleByRequestUrl(response.config && response.config.url)
        MessageBox.confirm('登录状态已失效，可重新登录后继续操作。', '确定登出', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          // 只清理出错接口所属端登录态，不影响另一端
          store.dispatch('FedLogOut', { role: failedRole }).then(() => {
            location.reload()
          })
        })
      }
      return Promise.reject(res)
    }

    return response.data
  },
  error => {
    let errorMessage = error.message || '请求失败'

    if (error.response) {
      const status = error.response.status
      const data = error.response.data
      if (data && data.message) {
        errorMessage = data.message
      } else if (status === 500) {
        errorMessage = '服务器内部错误(500)，请检查后端日志'
      } else if (status === 404) {
        errorMessage = '接口不存在(404)'
      } else if (status === 400) {
        errorMessage = '请求参数错误(400)'
      }
    }

    const silentError = error.config && error.config.silent
    if (!silentError) {
      Message({
        message: errorMessage,
        type: 'error',
        duration: 5000
      })
    }

    return Promise.reject(error)
  }
)

export default service
