// src/services/api.service.js

import axios from 'axios';

// IMPORTANT: Ensure this matches your Spring Boot URL
const API_URL = 'http://localhost:8080/api'; 

const apiClient = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Interceptor to add the JWT token to every request
apiClient.interceptors.request.use(config => {
    const token = localStorage.getItem('userToken');
    if (token) {
        // Add the Authorization header for protected routes
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export default apiClient;