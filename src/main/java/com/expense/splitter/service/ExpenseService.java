package com.expense.splitter.service;

import com.expense.splitter.dto.ExpenseRequest;
import com.expense.splitter.dto.ExpenseResponse;
import com.expense.splitter.exception.ResourceNotFoundException;
import com.expense.splitter.model.Expense;
import com.expense.splitter.model.Person;
import com.expense.splitter.repository.ExpenseRepository;
import com.expense.splitter.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final PersonRepository personRepository;
    
    // Constructor injection
    public ExpenseService(ExpenseRepository expenseRepository, PersonRepository personRepository) {
        this.expenseRepository = expenseRepository;
        this.personRepository = personRepository;
    }
    
    public ExpenseResponse createExpense(ExpenseRequest request) {
        Person paidByPerson = personRepository.findById(request.getPaidBy())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + request.getPaidBy()));
        
        for (String personId : request.getSplitBetween()) {
            if (!personRepository.existsById(personId)) {
                throw new ResourceNotFoundException("Person not found with id: " + personId);
            }
        }
        
        Expense expense = new Expense();
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setPaidBy(request.getPaidBy());
        expense.setSplitBetween(request.getSplitBetween());
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        
        Expense saved = expenseRepository.save(expense);
        return mapToResponse(saved);
    }
    
    public List<ExpenseResponse> getAllExpenses() {
        return expenseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public ExpenseResponse getExpenseById(String id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        return mapToResponse(expense);
    }
    
    public ExpenseResponse updateExpense(String id, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with id: " + id));
        
        if (!personRepository.existsById(request.getPaidBy())) {
            throw new ResourceNotFoundException("Person not found with id: " + request.getPaidBy());
        }
        
        for (String personId : request.getSplitBetween()) {
            if (!personRepository.existsById(personId)) {
                throw new ResourceNotFoundException("Person not found with id: " + personId);
            }
        }
        
        expense.setDescription(request.getDescription());
        expense.setAmount(request.getAmount());
        expense.setPaidBy(request.getPaidBy());
        expense.setSplitBetween(request.getSplitBetween());
        expense.setUpdatedAt(LocalDateTime.now());
        
        Expense updated = expenseRepository.save(expense);
        return mapToResponse(updated);
    }
    
    public void deleteExpense(String id) {
        if (!expenseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
    
    public Map<String, Double> calculateBalances() {
        Map<String, Double> balances = new HashMap<>();
        List<Expense> expenses = expenseRepository.findAll();
        
        personRepository.findAll().forEach(person -> balances.put(person.getId(), 0.0));
        
        for (Expense expense : expenses) {
            String paidBy = expense.getPaidBy();
            List<String> splitBetween = expense.getSplitBetween();
            double perPersonAmount = expense.getAmount() / splitBetween.size();
            
            balances.put(paidBy, balances.getOrDefault(paidBy, 0.0) + expense.getAmount());
            
            for (String personId : splitBetween) {
                balances.put(personId, balances.getOrDefault(personId, 0.0) - perPersonAmount);
            }
        }
        
        return balances;
    }
    
    public Map<String, Map<String, Object>> getSummary() {
        Map<String, Double> balances = calculateBalances();
        Map<String, Map<String, Object>> summary = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : balances.entrySet()) {
            Person person = personRepository.findById(entry.getKey()).orElse(null);
            if (person != null) {
                Map<String, Object> personData = new HashMap<>();
                personData.put("name", person.getName());
                personData.put("email", person.getEmail());
                personData.put("balance", Math.round(entry.getValue() * 100.0) / 100.0);
                summary.put(entry.getKey(), personData);
            }
        }
        
        return summary;
    }
    
    private ExpenseResponse mapToResponse(Expense expense) {
        Person paidByPerson = personRepository.findById(expense.getPaidBy()).orElse(null);
        
        List<String> splitBetweenNames = expense.getSplitBetween().stream()
                .map(id -> personRepository.findById(id)
                        .map(Person::getName)
                        .orElse("Unknown"))
                .collect(Collectors.toList());
        
        double perPersonAmount = expense.getAmount() / expense.getSplitBetween().size();
        
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getPaidBy(),
                paidByPerson != null ? paidByPerson.getName() : "Unknown",
                expense.getSplitBetween(),
                splitBetweenNames,
                Math.round(perPersonAmount * 100.0) / 100.0,
                expense.getCreatedAt(),
                expense.getUpdatedAt()
        );
    }
}

