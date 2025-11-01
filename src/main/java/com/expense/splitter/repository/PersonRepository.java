package com.expense.splitter.repository;

import com.expense.splitter.model.Person;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<Person, String> {
    boolean existsByEmail(String email);
}
