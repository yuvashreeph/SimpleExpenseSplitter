<template>
  <div class="max-w-3xl mx-auto p-4 md:p-8 font-sans bg-white shadow-2xl rounded-xl mt-10">
    <h2 class="text-3xl font-bold text-center mb-6 text-blue-700">Group Members</h2>

    <button @click="isModalOpen = true" class="bg-blue-600 text-white px-5 py-2 rounded-lg shadow-md hover:bg-blue-700 transition duration-200 mb-6">
        + Add New Member
    </button>
    
    <p v-if="loading" class="text-center py-4 text-gray-600">Loading members...</p>
    <p v-else-if="errorMessage" class="text-red-700 bg-red-100 p-3 rounded-lg mb-6 text-center font-medium">{{ errorMessage }}</p>
    
    <div v-else class="space-y-3">
        <div v-for="person in persons" :key="person.id" class="flex justify-between items-center p-4 bg-gray-50 border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition duration-200">
            <span class="text-gray-700 font-medium">
                {{ person.name }} 
                <span class="text-sm text-gray-500">({{ person.email }})</span>
            </span>
            
            <div class="flex space-x-3">
                <button @click="editPerson(person)" class="bg-yellow-500 text-white px-3 py-1 text-sm rounded-md hover:bg-yellow-600 transition duration-150">Edit</button>
                <button @click="deletePerson(person.id)" class="bg-red-600 text-white px-3 py-1 text-sm rounded-md hover:bg-red-700 transition duration-150">Delete</button>
            </div>
        </div>
        
        <p v-if="!persons.length && !loading" class="text-gray-500 italic py-4 text-center">No members added yet.</p>
    </div>

    <div v-if="isModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
      <div class="bg-white p-6 rounded-xl w-full max-w-md shadow-2xl animate-fade-in">
        <h3 class="text-xl font-semibold text-center mb-5 text-blue-700">
            {{ editingPerson.id ? 'Edit Member' : 'Add New Member' }}
        </h3>
        
        <div class="space-y-4">
            <input 
                type="text" 
                v-model="editingPerson.name" 
                placeholder="Name" 
                required 
                class="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 transition"
            />
            <input 
                type="email" 
                v-model="editingPerson.email" 
                placeholder="Email" 
                required 
                class="w-full p-3 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500 transition"
            />
        </div>

        <div class="flex justify-end space-x-3 mt-6">
            <button @click="savePerson" class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition duration-150 font-medium disabled:opacity-50" :disabled="!editingPerson.name || !editingPerson.email">
                Save
            </button>
            <button @click="closeModal" class="bg-gray-300 text-gray-800 px-4 py-2 rounded-lg hover:bg-gray-400 transition duration-150 font-medium">
                Cancel
            </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import personService from '@/services/person.service'; // Correct service import

const persons = ref([]);
const loading = ref(true);
const errorMessage = ref(null);
const isModalOpen = ref(false);
const initialPersonState = { id: null, name: '', email: '' };
const editingPerson = ref({ ...initialPersonState });

const fetchPersons = async () => {
  try {
    const response = await personService.getAll();
    persons.value = response.data;
    loading.value = false;
    errorMessage.value = null; // Clear previous errors on success
  } catch (error) {
    console.error('Failed to fetch persons:', error);
    // Display a friendlier message
    errorMessage.value = 'Failed to load group members. Check your connection or login status.';
    loading.value = false;
  }
};

const closeModal = () => {
    isModalOpen.value = false;
    editingPerson.value = { ...initialPersonState }; // Reset form
};

const editPerson = (person) => {
    editingPerson.value = { ...person };
    isModalOpen.value = true;
};

const savePerson = async () => {
    // Basic form validation check
    if (!editingPerson.value.name || !editingPerson.value.email) return;

    try {
        if (editingPerson.value.id) {
            // Update existing
            await personService.update(editingPerson.value.id, editingPerson.value);
        } else {
            // Create new
            await personService.create(editingPerson.value);
        }
        await fetchPersons(); // Refresh list
        closeModal();
    } catch (error) {
        // More detailed error handling for the user
        errorMessage.value = `Failed to save member. Status: ${error.response?.status || 'Unknown'}.`;
        console.error('Failed to save person:', error);
    }
};

const deletePerson = async (id) => {
    if (confirm('Are you sure you want to delete this member? This cannot be undone.')) {
        try {
            await personService.delete(id);
            await fetchPersons(); // Refresh list
        } catch (error) {
            errorMessage.value = `Failed to delete member. Status: ${error.response?.status || 'Unknown'}.`;
            console.error('Failed to delete person:', error);
        }
    }
};

onMounted(fetchPersons);
</script>

<style scoped>
/*
This minimal style block is necessary to include the Tailwind directives and the custom keyframe 
animation, which provides a smooth user experience for the modal.
*/
@import "tailwindcss";
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.animate-fade-in {
  animation: fadeIn 0.3s ease-out forwards;
}
</style>