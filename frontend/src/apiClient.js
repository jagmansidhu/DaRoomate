import axios from 'axios';
import { getCookie } from './webpages/utils/cookies';

const apiClient = axios.create({
    baseURL: process.env.REACT_APP_BASE_API_URL,
    withCredentials: true,
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-CSRF-TOKEN',
});

apiClient.interceptors.request.use((config) => {
    const method = (config.method || 'get').toLowerCase();
    if (['post', 'put', 'patch', 'delete'].includes(method)) {
        const csrfToken = getCookie('XSRF-TOKEN');
        if (csrfToken) {
            config.headers['X-CSRF-TOKEN'] = csrfToken;
        }
    }
    return config;
});

apiClient.interceptors.response.use((response) => response, (error) => {
    if (error.response && error.response.status === 401) {
        const msg = error.response.data?.message || '';
        const isAuthError = msg.toLowerCase().includes('log in') || msg.toLowerCase().includes('unauthorized');
        if (isAuthError && !['/', '/login', '/register', '/verify'].includes(window.location.pathname)) {
            ['appUser', 'appRooms', 'appChores', 'appUtilities', 'appEvents', 'appAuth'].forEach(k => localStorage.removeItem(k));
            window.location.href = '/login';
        }
    }
    return Promise.reject(error);
});

export default apiClient;
