package com.expense.splitter.controller;

import com.expense.splitter.dto.ExpenseRequest;
import com.expense.splitter.dto.ExpenseResponse;
import com.expense.splitter.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Expense CRUD operations and summary calculations.
 * Provides endpoints for managing expenses and viewing balances.
 */
@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "http://localhost:5173")
public class ExpenseController {

    private final ExpenseService expenseService;

    // ✅ Manual constructor (so no Lombok required)
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Create a new expense.
     * POST /api/expenses
     * Returns 201 CREATED with expense details.
     */
    @PostMapping
    public ResponseEntity<ExpenseResponse> createExpense(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all expenses.
     * GET /api/expenses
     * Returns 200 OK with a list of all expenses.
     */
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getAllExpenses() {
        List<ExpenseResponse> expenses = expenseService.getAllExpenses();
        return ResponseEntity.ok(expenses);
    }

    /**
     * Get an expense by ID.
     * GET /api/expenses/{id}
     * Returns 200 OK with expense details or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable String id) {
        ExpenseResponse response = expenseService.getExpenseById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing expense.
     * PUT /api/expenses/{id}
     * Returns 200 OK with updated expense details.
     */
    @PutMapping("/{id}")
    public ResponseEntity<ExpenseResponse> updateExpense(
            @PathVariable String id,
            @Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse response = expenseService.updateExpense(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an expense.
     * DELETE /api/expenses/{id}
     * Returns 204 NO CONTENT on successful deletion.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get a summary of all balances with person details.
     * GET /api/expenses/summary
     * Returns 200 OK with a map of person ID -> name, email, balance.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Map<String, Object>>> getSummary() {
        Map<String, Map<String, Object>> summary = expenseService.getSummary();
        return ResponseEntity.ok(summary);
    }

    /**
     * Get raw numeric balances (person ID -> net balance).
     * GET /api/expenses/balances
     * Returns 200 OK with a map of person ID -> balance amount.
     */
    @GetMapping("/balances")
    public ResponseEntity<Map<String, Double>> getBalances() {
        Map<String, Double> balances = expenseService.calculateBalances();
        return ResponseEntity.ok(balances);
    }
}
