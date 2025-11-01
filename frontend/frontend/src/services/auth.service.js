// src/services/auth.service.js

import axios from 'axios';
const API_URL = 'http://localhost:8080/api/auth/';

class AuthService {
    async login(username, password) {
        const response = await axios.post(API_URL + 'login', {
            username,
            password
        });

        // If login is successful, store the token
        if (response.data.token) {
            localStorage.setItem('userToken', response.data.token);
            localStorage.setItem('username', response.data.username);
        }
        return response.data;
    }

    logout() {
        localStorage.removeItem('userToken');
        localStorage.removeItem('username');
    }

    async register(username, password) {
        // Registration doesn't return a token
        return axios.post(API_URL + 'register', {
            username,
            password
        });
    }

    getAuthHeader() {
        return localStorage.getItem('userToken');
    }
}

export default new AuthService();