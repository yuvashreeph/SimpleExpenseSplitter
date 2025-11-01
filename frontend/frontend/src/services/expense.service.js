import apiClient from './api.service';

class ExpenseService {
  // Fetch all expenses
  getAll() {
    return apiClient.get('/expenses');
  }

  // Get a single expense by ID
  getById(id) {
    return apiClient.get(`/expenses/${id}`);
  }

  // Create a new expense
  create(expense) {
    return apiClient.post('/expenses', expense);
  }

  // Update an existing expense
  update(id, expense) {
    return apiClient.put(`/expenses/${id}`, expense);
  }

  // Delete an expense
  delete(id) {
    return apiClient.delete(`/expenses/${id}`);
  }

  // Get summary (person ID → name, email, balance)
  getSummary() {
    return apiClient.get('/expenses/summary');
  }

  // Get numeric balances
  getBalances() {
    return apiClient.get('/expenses/balances');
  }
}

export default new ExpenseService();
