<template>
  <div class="max-w-4xl mx-auto p-4 md:p-6 font-sans">
    <h2 class="text-3xl font-bold mb-6 text-gray-800">Group Expenses</h2>

    <p v-if="loading" class="text-center py-4 text-lg text-gray-600">Loading data...</p>
    <p v-else-if="errorMessage" class="text-red-700 bg-red-100 p-3 rounded-lg mb-6">
      {{ errorMessage }}
    </p>

    <div v-else>
      <p v-if="!members.length" class="text-yellow-700 bg-yellow-100 p-3 rounded-lg mb-6">
        ⚠️ Please add members first using the Members tab before recording expenses.
      </p>

      <button
        v-if="members.length"
        class="bg-blue-600 text-white px-6 py-3 rounded-xl shadow-lg hover:bg-blue-700 transition duration-150 mb-6 disabled:opacity-50"
        @click="openExpenseModal"
      >
        + Add New Expense
      </button>

      <div class="mt-4 space-y-3">
        <p v-if="!expenses.length" class="text-gray-500 italic py-4">No expenses recorded yet.</p>

        <div
          v-for="expense in expenses"
          :key="expense.id"
          class="flex justify-between items-center p-4 bg-white border border-gray-200 rounded-xl shadow-sm hover:shadow-lg transition duration-200"
        >
          <div class="flex-grow flex justify-between items-center cursor-pointer" @click="viewExpenseDetails(expense)">
            <span class="font-medium text-lg text-gray-700">
              {{ expense.description }} - <span class="text-blue-600 font-bold">${{ expense.amount.toFixed(2) }}</span>
              (Paid by <span class="text-gray-600">{{ expense.paidByName }}</span>)
            </span>
          </div>

          <div class="flex space-x-2 ml-4">
            <button @click="viewExpenseDetails(expense)" class="bg-blue-500 text-white px-3 py-2 rounded-lg text-sm hover:bg-blue-600 transition duration-150">
              View
            </button>
            <button @click.stop="editExpense(expense)" class="bg-gray-300 text-gray-800 px-3 py-2 rounded-lg text-sm hover:bg-gray-400 transition duration-150">
              Edit
            </button>
            <button @click.stop="deleteExpense(expense.id)" class="bg-red-600 text-white px-3 py-2 rounded-lg text-sm hover:bg-red-700 transition duration-150">
              Delete
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="Object.keys(balances).length" class="mt-8 p-6 bg-gray-50 rounded-xl shadow-inner">
      <h3 class="text-2xl font-semibold mb-4 text-blue-700">💰 Balance Summary</h3>

      <div
        v-for="(amount, personId) in balances"
        :key="personId"
        class="flex justify-between py-2 border-b border-gray-200 last:border-b-0"
      >
        <strong class="text-gray-700">{{ getMemberName(personId) }}</strong>
        <span
          :class="{
            'text-green-600 font-semibold': amount > 0,
            'text-red-600 font-semibold': amount < 0,
            'text-gray-500': amount === 0
          }"
        >
          {{ formatCurrency(amount) }}
        </span>
      </div>
    </div>

    <div v-if="isModalOpen" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
      <div class="bg-white p-6 md:p-8 rounded-xl w-full max-w-lg shadow-2xl relative">
        <h3 class="text-2xl font-bold mb-6 text-gray-800">{{ isEditing ? 'Edit Expense' : 'Record New Expense' }}</h3>
        <form @submit.prevent="saveExpense" class="space-y-4">
          <div class="form-group">
            <label class="block mb-1 font-semibold text-gray-700">Description:</label>
            <input type="text" v-model="currentExpense.description" required class="w-full p-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500" />
          </div>

          <div class="form-group">
            <label class="block mb-1 font-semibold text-gray-700">Amount:</label>
            <input
              type="number"
              v-model.number="currentExpense.amount"
              required
              min="0.01"
              step="0.01"
              class="w-full p-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          <div class="form-group">
            <label class="block mb-1 font-semibold text-gray-700">Paid By:</label>
            <select v-model="currentExpense.paidBy" required class="w-full p-2 border border-gray-300 rounded-lg focus:ring-blue-500 focus:border-blue-500">
              <option :value="null" disabled>Select member who paid</option>
              <option
                v-for="member in members"
                :key="member.id"
                :value="member.id"
              >
                {{ member.name }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label class="block mb-2 font-semibold text-gray-700">Split Between (Select all participants):</label>
            <div class="flex flex-wrap gap-2">
              <div
                v-for="member in members"
                :key="member.id"
                class="px-3 py-1 border border-gray-300 rounded-lg cursor-pointer transition duration-150 text-sm"
                :class="{ 'bg-blue-100 border-blue-600 font-semibold text-blue-600': currentExpense.splitBetween.includes(member.id), 'hover:bg-gray-100': !currentExpense.splitBetween.includes(member.id) }"
                @click="toggleMember(member.id)"
              >
                {{ member.name }}
              </div>
            </div>

            <p
              v-if="currentExpense.splitBetween.length === 0"
              class="text-yellow-600 text-sm mt-2"
            >
              Select at least one person to split the expense.
            </p>
          </div>

          <div class="flex justify-end space-x-3 pt-4">
            <button
              type="submit"
              class="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 transition duration-150 disabled:bg-blue-300 disabled:cursor-not-allowed"
              :disabled="
                !currentExpense.description ||
                !currentExpense.amount ||
                !currentExpense.paidBy ||
                currentExpense.splitBetween.length === 0 ||
                currentExpense.amount <= 0
              "
            >
              {{ isEditing ? 'Update Expense' : 'Record Expense' }}
            </button>
            <button type="button" @click="closeExpenseModal" class="bg-gray-300 text-gray-800 px-5 py-2 rounded-lg hover:bg-gray-400 transition duration-150">
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>

    <div v-if="selectedExpense" class="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50 p-4">
      <div class="bg-white p-6 md:p-8 rounded-xl w-full max-w-sm shadow-2xl relative">
        <div class="flex justify-between items-center mb-6">
          <h3 class="text-2xl font-bold text-gray-800">Expense Details</h3>
          <button class="text-gray-500 text-3xl leading-none hover:text-gray-700" @click="selectedExpense = null">&times;</button>
        </div>

        <div class="space-y-3">
          <div class="flex justify-between py-2 border-b border-gray-100">
            <strong class="text-gray-700">Description:</strong>
            <span>{{ selectedExpense.description }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <strong class="text-gray-700">Amount:</strong>
            <span class="font-bold text-blue-600">${{ selectedExpense.amount.toFixed(2) }}</span>
          </div>
          <div class="flex justify-between py-2 border-b border-gray-100">
            <strong class="text-gray-700">Paid By:</strong>
            <span>{{ selectedExpense.paidByName }}</span>
          </div>
          <div class="py-2">
            <strong class="block mb-1 text-gray-700">Split Between:</strong>
            <span class="text-sm block text-gray-600">{{ selectedExpense.splitBetweenNames.join(', ') }}</span>
          </div>
        </div>

        <div class="flex justify-end pt-6">
          <button type="button" @click="selectedExpense = null" class="bg-blue-600 text-white px-5 py-2 rounded-lg hover:bg-blue-700 transition duration-150">
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import expenseService from '@/services/expense.service';
import personService from '@/services/person.service';

const router = useRouter();
const expenses = ref([]);
const members = ref([]);
const balances = ref({});
const loading = ref(true);
const errorMessage = ref(null);
const isModalOpen = ref(false);
const selectedExpense = ref(null);
const isEditing = ref(false);

const initialExpenseState = {
  id: null, // Added ID for clarity
  description: '',
  amount: null,
  paidBy: null,
  splitBetween: [],
};
const currentExpense = ref({ ...initialExpenseState });

const getMemberName = (id) => {
  const member = members.value.find((m) => m.id === id);
  return member ? member.name : 'Unknown';
};

const formatCurrency = (num) =>
  (num > 0 ? '+' : num < 0 ? '-' : '') + '$' + Math.abs(num).toFixed(2);

// Function to enrich expense data with names
const enrichExpenses = (rawExpenses) => {
  return rawExpenses.map((expense) => {
    // 1. Get Paid By Name
    const paidByName = getMemberName(expense.paidBy);
    
    // 2. Get Split Between Names
    const splitBetweenNames = expense.splitBetween.map(getMemberName);

    return {
      ...expense,
      paidByName,
      splitBetweenNames, // This is the property required by the details modal
    };
  });
};


const fetchMembers = async () => {
  try {
    // ⚠️ IMPORTANT: Members must be fetched before expenses to allow for name mapping.
    const response = await personService.getAll();
    members.value = response.data;
  } catch (error) {
    errorMessage.value = 'Failed to load members.';
    console.error('Member fetch error:', error);
  }
};

const fetchExpenses = async () => {
  // We rely on members.value being populated here
  if (!members.value.length) {
    // If members failed to load, don't try to fetch expenses yet.
    return;
  }
  
  try {
    const response = await expenseService.getAll();
    // 🚀 Correction: Enrich the data immediately after fetching
    expenses.value = enrichExpenses(response.data); 
  } catch (error) {
    errorMessage.value = 'Failed to load expenses.';
    console.error('Expense fetch error:', error);
  }
};

const fetchBalances = async () => {
  try {
    const response = await expenseService.getBalances();
    balances.value = response.data;
  } catch (error) {
    console.error('Error fetching balances:', error);
  }
};

const initializeData = async () => {
  loading.value = true;
  
  // 1. Fetch Members first
  await fetchMembers(); 

  // 2. Fetch Expenses and Balances only if members were loaded (or continue with errors)
  await Promise.all([fetchExpenses(), fetchBalances()]);
  
  loading.value = false;
};

const openExpenseModal = () => {
  isEditing.value = false;
  currentExpense.value = { ...initialExpenseState };
  isModalOpen.value = true;
};

const closeExpenseModal = () => {
  isModalOpen.value = false;
};

const viewExpenseDetails = (expense) => {
  // Expense is already enriched here
  selectedExpense.value = expense;
};

// Handle edit
const editExpense = (expense) => {
  isEditing.value = true;
  // Use the raw IDs from the expense object for the form input binding
  currentExpense.value = {
    id: expense.id, 
    description: expense.description,
    amount: expense.amount,
    paidBy: expense.paidBy,
    splitBetween: [...expense.splitBetween],
  };
  isModalOpen.value = true;
};


// Handle delete
const deleteExpense = async (id) => {
  // ⚠️ Important: Replaced browser default confirm() with a placeholder log/logic
  // In a real app, you must use a custom modal for confirmation.
  if (confirm('Are you sure you want to delete this expense?')) { 
    try {
      await expenseService.delete(id);
      await initializeData();
    } catch (error) {
      console.error('Delete error:', error);
      errorMessage.value = 'Failed to delete expense.';
    }
  }
};

// Save (create/update)
const saveExpense = async () => {
  const expenseData = {
    description: currentExpense.value.description,
    amount: currentExpense.value.amount,
    paidBy: currentExpense.value.paidBy,
    splitBetween: currentExpense.value.splitBetween,
  };

  try {
    if (isEditing.value) {
      // 🚀 Correction: Use currentExpense.value.id directly
      await expenseService.update(currentExpense.value.id, expenseData);
    } else {
      await expenseService.create(expenseData);
    }

    closeExpenseModal();
    // Reinitialize data to refresh lists and balances
    await initializeData();
  } catch (error) {
    console.error('Save error:', error.response?.data || error);
    // Display error message from backend if available
    errorMessage.value = error.response?.data?.message || 'Failed to save expense.';
  }
};

const toggleMember = (memberId) => {
  const index = currentExpense.value.splitBetween.indexOf(memberId);
  if (index === -1) {
    currentExpense.value.splitBetween.push(memberId);
  } else {
    currentExpense.value.splitBetween.splice(index, 1);
  }
};

onMounted(initializeData);
</script>

<style>
/* In a typical Vue/Vite/Tailwind setup, you would have a separate main CSS file (e.g., src/style.css) 
where you place the directives. Since I can only modify this single file, 
I am including the necessary Tailwind directives here, which is often done 
in development for quick setups.

The previous custom CSS has been removed, as the template now uses Tailwind utility classes.
*/
@import "tailwindcss";
/* Any custom styles that need to be applied beyond utilities (like utility-first components 
or overrides) would go here, but for this refactoring, we rely entirely on Tailwind classes 
in the template.
*/
</style>