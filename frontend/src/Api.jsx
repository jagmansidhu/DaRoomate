import axios from 'axios';

const createApi = (token) => {
    const instance = axios.create({
        baseURL: 'http://localhost:8085/api',
    });

    if (token) {
        instance.interceptors.request.use(
            (config) => {
                config.headers['Authorization'] = `Bearer ${token}`;
                return config;
            },
            (error) => Promise.reject(error)
        );
    }

    return instance;
};

export default createApi;