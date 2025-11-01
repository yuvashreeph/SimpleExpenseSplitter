package com.expense.splitter.repository;

import com.expense.splitter.model.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for managing Expense data.
 */
@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
}
