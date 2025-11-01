package com.expense.splitter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class ExpenseRequest {
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Double amount;
    
    @NotBlank(message = "Paid by person ID is required")
    private String paidBy;
    
    @NotEmpty(message = "Split between list cannot be empty")
    private List<String> splitBetween;
    
    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    
    public List<String> getSplitBetween() { return splitBetween; }
    public void setSplitBetween(List<String> splitBetween) { this.splitBetween = splitBetween; }
}
