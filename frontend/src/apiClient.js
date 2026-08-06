import axios from 'axios';
import { getCookie } from './webpages/utils/cookies';

// Cross-origin SPAs cannot read the backend's XSRF-TOKEN cookie via document.cookie.
// Prefer the token returned in /user/status and /user/login JSON bodies.
let csrfTokenFromApi = null;

export function setCsrfToken(token) {
    csrfTokenFromApi = token || null;
}

const apiClient = axios.create({
    // Empty/undefined => same-origin (Railway: nginx proxies /api and /user to backend)
    baseURL: process.env.REACT_APP_BASE_API_URL || '',
    withCredentials: true,
    xsrfCookieName: 'XSRF-TOKEN',
    xsrfHeaderName: 'X-CSRF-TOKEN',
});

apiClient.interceptors.request.use((config) => {
    const method = (config.method || 'get').toLowerCase();
    if (['post', 'put', 'patch', 'delete'].includes(method)) {
        const csrfToken = csrfTokenFromApi || getCookie('XSRF-TOKEN');
        if (csrfToken) {
            config.headers['X-CSRF-TOKEN'] = csrfToken;
        }
    }
    return config;
});

apiClient.interceptors.response.use((response) => {
    const token = response?.data?.csrfToken;
    if (typeof token === 'string' && token.length > 0) {
        setCsrfToken(token);
    }
    return response;
}, (error) => {
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
