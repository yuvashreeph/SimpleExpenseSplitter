// src/router/index.js

import { createRouter, createWebHistory } from 'vue-router';

// 1. Import your existing view components
import Login from '../views/Login.vue';
import Register from '../views/Register.vue';
import ExpenseList from '../views/ExpenseList.vue';
import PersonList from '../views/PersonList.vue';

// Utility to check for the JWT token
const isLoggedIn = () => {
    // Check if a token exists in local storage
    return !!localStorage.getItem('userToken');
};

const routes = [
    {
        path: '/',
        name: 'Home',
        redirect: '/expenses' // Redirect base path to the main expense view
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/expenses',
        name: 'Expenses',
        component: ExpenseList,
        meta: { requiresAuth: true } // Protected route
    },
    {
        path: '/persons',
        name: 'Persons',
        component: PersonList,
        meta: { requiresAuth: true } // Protected route
    }
    // You would also add a '/summary' route here
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

// Global navigation guard
router.beforeEach((to, from, next) => {
    // Check if the route requires authentication and if the user is NOT logged in
    if (to.meta.requiresAuth && !isLoggedIn()) {
        // Redirect to the login page
        next('/login');
    } else {
        next(); // Proceed as normal
    }
});

export default router;