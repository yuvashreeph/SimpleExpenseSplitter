<template>
  <div id="app">
    <nav class="navbar">
      <router-link to="/expenses" class="nav-item">Expenses</router-link>
      <router-link to="/persons" class="nav-item">Members</router-link>
      <div class="spacer"></div>
      
      <div v-if="isLoggedIn">
        <span class="welcome-msg">Hello, {{ username }}</span>
        <button @click="logOut" class="nav-button">Logout</button>
      </div>
      <div v-else>
        <router-link to="/login" class="nav-item">Login</router-link>
        <router-link to="/register" class="nav-item">Register</router-link>
      </div>
    </nav>

    <div class="container">
      <router-view />
    </div>
  </div>
</template>

<script>
import AuthService from './services/auth.service';
import { useRouter } from 'vue-router';
import { ref, computed } from 'vue';

export default {
  name: 'App',
  setup() {
    const router = useRouter();
    
    // Reactive properties for login status
    const isLoggedIn = ref(AuthService.getAuthHeader() != null);
    const username = ref(localStorage.getItem('username') || '');

    // Recheck auth status when route changes (optional, but good practice)
    router.afterEach(() => {
        isLoggedIn.value = AuthService.getAuthHeader() != null;
        username.value = localStorage.getItem('username') || '';
    });

const logOut = () => {
  if (confirm("Are you sure you want to log out?")) {
    AuthService.logout();
    isLoggedIn.value = false;
    username.value = '';

    alert("You have been logged out successfully.");
    router.push('/login');
  }
};


    return {
      isLoggedIn: computed(() => isLoggedIn.value),
      username: computed(() => username.value),
      logOut,
    };
  }
};
</script>

<style>
/* Basic Styles (you'll need to expand this) */
.navbar {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 12px 24px;
  background: linear-gradient(135deg, #064687, #4d4287);
  color: #fff;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

.nav-item {
  color: #fff;
  text-decoration: none;
  font-weight: 500;
  padding: 8px 16px;
  border-radius: 8px;
  transition: background 0.3s ease, transform 0.1s ease;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.spacer {
  flex-grow: 1;
}

.welcome-msg {
  margin-right: 10px;
  font-weight: 500;
  color: #f0f0f0;
}

.nav-button {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: #fff;
  padding: 8px 14px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.3s ease, transform 0.1s ease;
}

.nav-button:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.container {
  padding: 0px;
}
#app {
  padding: 0px;
  margin: 0px;
  border: 0px;;
  min-height: 100vh;
  background: linear-gradient(135deg, #74b9ff, #a29bfe); /* Same gradient as the navbar */
}
</style>