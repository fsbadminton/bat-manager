import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '../store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  //baseURL: process.env.BASE_API, // api的base_url
  baseURL: '/api', // 使用代理
  timeout: 5000, // 请求超时时间
  withCredentials: false // 本地调试先关掉
})

// request拦截器
service.interceptors.request.use(config => {
  if (store.getters.token) {
    config.headers['Authorization'] = getToken() // 让每个请求携带自定义token 请根据实际情况自行修改
  }
  // 调试：打印请求 URL 和参数
  if (config.url && config.url.includes('/product/page')) {
    console.log('=== 产品分页请求 ===');
    console.log('请求 URL:', config.baseURL + config.url);
    console.log('请求参数 (params):', config.params);
    console.log('完整请求配置:', JSON.stringify({
      url: config.url,
      method: config.method,
      params: config.params
    }, null, 2));
  }
  if (config.url && config.url.includes('/admin/product/getById')) {
    console.log('=== 产品详情请求 ===');
    console.log('请求 URL:', config.baseURL + config.url);
    console.log('请求方法:', config.method);
    console.log('请求参数 (params):', config.params);
  }
  if (config.url && config.url.includes('/admin/product/update')) {
    console.log('=== 产品更新请求 ===');
    console.log('请求 URL:', config.baseURL + config.url);
    console.log('请求方法:', config.method);
    console.log('请求体 (data):', config.data);
  }
  return config
}, error => {
  // Do something with request error
  console.log(error) // for debug
  Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
  response => {
  /**
  * code为非200是抛错 可结合自己业务进行修改
  */
    const res = response.data
    // 如果请求配置里设置了 silent，则不显示错误提示，交由调用方处理
    const silent = response.config && response.config.silent
    if (res.code !== 1) {
      if (!silent) {
        Message({
          message: res.msg || res.message,
          type: 'error',
          duration: 3 * 1000
        })
      }

      // 401:未登录;
      if (res.code === 401 && !silent) {
        MessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确定登出', {
          confirmButtonText: '重新登录',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          store.dispatch('FedLogOut').then(() => {
            location.reload()// 为了重新实例化vue-router对象 避免bug
          })
        })
      }
      // 将完整的响应数据作为错误对象抛出，这样调用方可以获取 msg 等字段
      return Promise.reject(res)
    } else {
      return response.data
    }
  },
  error => {
    console.log('err' + error)// for debug
    // 获取更详细的错误信息
    let errorMessage = error.message || '请求失败';
    if (error.response) {
      // 服务器返回了错误响应
      const status = error.response.status;
      const data = error.response.data;
      console.error('错误响应状态:', status);
      console.error('错误响应数据:', data);
      
      if (data && data.message) {
        errorMessage = data.message;
      } else if (status === 500) {
        errorMessage = '服务器内部错误 (500)，请检查后端日志';
      } else if (status === 404) {
        errorMessage = '接口不存在 (404)';
      } else if (status === 400) {
        errorMessage = '请求参数错误 (400)';
      }
    }
    // 如果请求配置里设置了 silent，则不显示错误提示，交由调用方处理
    const silentError = error.config && error.config.silent
    if (!silentError) {
      Message({
        message: errorMessage,
        type: 'error',
        duration: 5 * 1000
      })
    }
    return Promise.reject(error)
  }
)

export default service
