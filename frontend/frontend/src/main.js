// src/main.js (Update this file)

import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // Import the new router file

// Assuming you have an existing CSS file for styling
// import './assets/main.css'

const app = createApp(App);

// Attach the router instance to the app
app.use(router);

app.mount('#app');