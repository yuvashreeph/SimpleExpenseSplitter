package com.expense.splitter.service;

import com.expense.splitter.dto.ExpenseRequest;
import com.expense.splitter.dto.ExpenseResponse;
import com.expense.splitter.exception.ResourceNotFoundException;
import com.expense.splitter.model.Expense;
import com.expense.splitter.model.Person;
import com.expense.splitter.repository.ExpenseRepository;
import com.expense.splitter.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Service tests for ExpenseService
 * Tests business logic and calculations
 */
@ExtendWith(MockitoExtension.class)
public class ExpenseServiceTest {
    
    @Mock
    private ExpenseRepository expenseRepository;
    
    @Mock
    private PersonRepository personRepository;
    
    @InjectMocks
    private ExpenseService expenseService;
    
    private Person person1;
    private Person person2;
    private Expense expense;
    
    @BeforeEach
    public void setUp() {
        person1 = new Person("p1", "John", "john@email.com",
                LocalDateTime.now(), LocalDateTime.now());
        person2 = new Person("p2", "Jane", "jane@email.com",
                LocalDateTime.now(), LocalDateTime.now());
        
        expense = new Expense("e1", "Dinner", 100.0, "p1",
                Arrays.asList("p1", "p2"),
                LocalDateTime.now(), LocalDateTime.now());
    }
    
    @Test
    public void testCreateExpense_Success() {
        ExpenseRequest request = new ExpenseRequest();
        request.setDescription("Dinner");
        request.setAmount(100.0);
        request.setPaidBy("p1");
        request.setSplitBetween(Arrays.asList("p1", "p2"));
        
        when(personRepository.findById("p1")).thenReturn(Optional.of(person1));
        when(personRepository.existsById("p1")).thenReturn(true);
        when(personRepository.existsById("p2")).thenReturn(true);
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);
        
        ExpenseResponse response = expenseService.createExpense(request);
        
        assertNotNull(response);
        assertEquals("Dinner", response.getDescription());
        assertEquals(100.0, response.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }
    
    @Test
    public void testCreateExpense_PersonNotFound() {
        ExpenseRequest request = new ExpenseRequest();
        request.setDescription("Dinner");
        request.setAmount(100.0);
        request.setPaidBy("invalid_id");
        request.setSplitBetween(Arrays.asList("p1", "p2"));
        
        when(personRepository.findById("invalid_id")).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            expenseService.createExpense(request);
        });
    }
    
    @Test
    public void testCalculateBalances() {
        when(personRepository.findAll()).thenReturn(Arrays.asList(person1, person2));
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense));
        
        Map balances = expenseService.calculateBalances();
        
        assertNotNull(balances);
        assertEquals(50.0, balances.get("p1")); // Paid 100, owes 50
        assertEquals(-50.0, balances.get("p2")); // Owes 50
    }
}