// src/utils/axios.js
import axios from 'axios'
import { Message } from 'element-ui'

// 配置后端基础路径（通过webpack-dev-server代理转发到8443）
const service = axios.create({
    baseURL: '/', // 开发环境通过代理转发，生产环境直接请求
    timeout: 5000 // 请求超时时间
})

// 请求拦截器（ token认证）
service.interceptors.request.use(
    config => {
        // 示例：添加 token 到请求头
        // if (localStorage.getItem('token')) {
        //   config.headers.Authorization = 'Bearer ' + localStorage.getItem('token')
        // }
        return config
    },
    error => {
        console.error('请求拦截器异常：', error)
        return Promise.reject(error)
    }
)

// 响应拦截器（统一处理响应结果）
service.interceptors.response.use(
    response => {
        return response
    },
    error => {
        console.error('响应异常：', error)
        Message.error('接口请求失败，请稍后重试')
        return Promise.reject(error)
    }
)

export default service