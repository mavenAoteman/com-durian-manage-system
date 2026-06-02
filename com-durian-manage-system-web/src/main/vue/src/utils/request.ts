import axios, { AxiosInstance, AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { ElMessage } from 'element-plus';

const service: AxiosInstance = axios.create({
    timeout: 30000,
    headers: { 'Content-Type': 'application/json' }
});

service.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('ms_token');
        if (token && config.headers) {
            config.headers.set('Authorization', `Bearer ${token}`);
        }
        return config;
    },
    (error: AxiosError) => Promise.reject(error)
);

service.interceptors.response.use(
    (response: AxiosResponse) => {
        if (response.status === 200) return response;
        return Promise.reject(response);
    },
    (error: AxiosError) => {
        const status = error.response?.status;
        const msg = (error.response?.data as any)?.message
                 || (error.response?.data as any)?.error
                 || error.message
                 || '请求失败';
        console.error('API Error:', msg, status);

        if (status === 401) {
            // 登录态丢失或过期：清掉本地状态并跳登录
            localStorage.removeItem('ms_token');
            localStorage.removeItem('ms_user');
            localStorage.removeItem('ms_keys');
            localStorage.removeItem('ms_username');
            // 避免 /login 页面自身的预检接口反复弹消息
            if (!location.hash.includes('/login')) {
                ElMessage.warning('登录已过期，请重新登录');
                location.hash = '#/login';
            }
        }
        return Promise.reject({ ...error, displayMessage: msg });
    }
);

export default service;
