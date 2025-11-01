package com.expense.splitter.controller;

import com.expense.splitter.dto.ExpenseRequest;
import com.expense.splitter.dto.ExpenseResponse;
import com.expense.splitter.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @Test
    @WithMockUser
    public void testCreateExpense_Success() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setDescription("Dinner");
        request.setAmount(100.0);
        request.setPaidBy("person1");
        request.setSplitBetween(Arrays.asList("person1", "person2"));

        ExpenseResponse response = new ExpenseResponse(
                "exp1",
                "Dinner",
                100.0,
                "person1",
                "John",
                Arrays.asList("person1", "person2"),
                Arrays.asList("John", "Jane"),
                50.0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(expenseService.createExpense(any(ExpenseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("exp1"))
                .andExpect(jsonPath("$.description").value("Dinner"))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    @WithMockUser
    public void testCreateExpense_ValidationError() throws Exception {
        ExpenseRequest request = new ExpenseRequest();
        request.setDescription(""); // Invalid: empty description
        request.setAmount(-10.0); // Invalid: negative amount

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    public void testGetAllExpenses() throws Exception {
        List<ExpenseResponse> expenses = Arrays.asList(
                new ExpenseResponse(
                        "exp1",
                        "Dinner",
                        100.0,
                        "p1",
                        "John",
                        Arrays.asList("p1", "p2"),
                        Arrays.asList("John", "Jane"),
                        50.0,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );

        when(expenseService.getAllExpenses()).thenReturn(expenses);

        mockMvc.perform(get("/api/expenses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Dinner"));
    }
}
