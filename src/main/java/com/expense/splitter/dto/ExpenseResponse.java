package com.expense.splitter.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ExpenseResponse {
    private String id;
    private String description;
    private Double amount;
    private String paidBy;
    private String paidByName;
    private List<String> splitBetween;
    private List<String> splitBetweenNames;
    private Double perPersonAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ExpenseResponse() {}
    
    public ExpenseResponse(String id, String description, Double amount, String paidBy,
                          String paidByName, List<String> splitBetween, List<String> splitBetweenNames,
                          Double perPersonAmount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.paidBy = paidBy;
        this.paidByName = paidByName;
        this.splitBetween = splitBetween;
        this.splitBetweenNames = splitBetweenNames;
        this.perPersonAmount = perPersonAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getPaidBy() { return paidBy; }
    public void setPaidBy(String paidBy) { this.paidBy = paidBy; }
    
    public String getPaidByName() { return paidByName; }
    public void setPaidByName(String paidByName) { this.paidByName = paidByName; }
    
    public List<String> getSplitBetween() { return splitBetween; }
    public void setSplitBetween(List<String> splitBetween) { this.splitBetween = splitBetween; }
    
    public List<String> getSplitBetweenNames() { return splitBetweenNames; }
    public void setSplitBetweenNames(List<String> splitBetweenNames) { this.splitBetweenNames = splitBetweenNames; }
    
    public Double getPerPersonAmount() { return perPersonAmount; }
    public void setPerPersonAmount(Double perPersonAmount) { this.perPersonAmount = perPersonAmount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}