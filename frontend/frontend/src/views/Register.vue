<template>
  <!-- Main container: centers content vertically and horizontally, full screen, background gradient -->
  <div class="flex items-center justify-center min-h-screen bg-gradient-to-br from-indigo-400 to-purple-500 p-4">
    <!-- Auth Card: white background, shadows, rounded corners -->
    <div class="bg-white p-8 md:p-10 rounded-2xl shadow-2xl w-full max-w-sm animate-fade-in-up">
      <h2 class="text-3xl font-bold text-center mb-6 text-gray-800">Register</h2>
      
      <form @submit.prevent="handleRegister" class="space-y-5">
        <!-- Username Field -->
        <div>
          <label for="username" class="block mb-2 font-medium text-gray-600">Username:</label>
          <input 
            type="text" 
            id="username" 
            v-model="username" 
            required 
            class="w-full p-3 border border-gray-300 rounded-lg focus:ring-purple-500 focus:border-purple-500 transition duration-150"
          />
        </div>

        <!-- Password Field -->
        <div>
          <label for="password" class="block mb-2 font-medium text-gray-600">Password:</label>
          <input 
            type="password" 
            id="password" 
            v-model="password" 
            required 
            class="w-full p-3 border border-gray-300 rounded-lg focus:ring-purple-500 focus:border-purple-500 transition duration-150"
          />
        </div>

        <!-- Register Button -->
        <button 
          type="submit" 
          :disabled="loading"
          class="w-full p-3 mt-4 bg-purple-600 text-white font-semibold rounded-lg shadow-md hover:bg-purple-700 transition duration-200 disabled:opacity-50 disabled:cursor-not-allowed"
        >
          {{ loading ? 'Registering...' : 'Register' }}
        </button>

        <!-- Message/Error Display -->
        <p v-if="message" 
          class="p-3 mt-4 rounded-lg text-center text-sm font-medium"
          :class="isError ? 'bg-red-100 text-red-700 border border-red-300' : 'bg-green-100 text-green-700 border border-green-300'"
        >
          {{ message }}
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import authService from '@/services/auth.service'

const router = useRouter()
const username = ref('')
const password = ref('')
const loading = ref(false)
const message = ref('')
const isError = ref(false)

const handleRegister = async () => {
  loading.value = true
  message.value = ''
  isError.value = false

  try {
    // NOTE: If using Firebase Auth, this would involve createUserWithEmailAndPassword
    await authService.register(username.value, password.value)
    isError.value = false
    message.value = 'Registration successful! Redirecting to login...'
    setTimeout(() => router.push('/login'), 2000)
  } catch (error) {
    console.error('Registration failed:', error)
    isError.value = true
    if (error.response?.data?.message) {
      message.value = error.response.data.message
    } else {
      message.value = 'Registration failed. Username may already be taken.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/*
This minimal style block is necessary to include the Tailwind directives and the custom keyframe 
animation, which cannot be expressed using standard utility classes.
*/
@import "tailwindcss";
@keyframes fadeInUp {
  from { 
    opacity: 0; 
    transform: translateY(20px); 
  }
  to { 
    opacity: 1; 
    transform: translateY(0); 
  }
}

.animate-fade-in-up {
  animation: fadeInUp 0.6s ease-out forwards;
}
</style>
