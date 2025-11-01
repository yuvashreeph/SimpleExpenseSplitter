package com.expense.splitter.controller;

import com.expense.splitter.dto.PersonRequest;
import com.expense.splitter.dto.PersonResponse;
import com.expense.splitter.service.PersonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for Person CRUD operations
 * Provides RESTful endpoints for managing persons
 */
@RestController
@RequestMapping("/api/persons")
@CrossOrigin(origins = "http://localhost:5173")
public class PersonController {

    private final PersonService personService;

    // ✅ Constructor injection (manual) to fix "blank final field not initialized" issue
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    /** Create a new person */
    @PostMapping
    public ResponseEntity<PersonResponse> createPerson(@Valid @RequestBody PersonRequest request) {
        PersonResponse response = personService.createPerson(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /** Get all persons */
    @GetMapping
    public ResponseEntity<List<PersonResponse>> getAllPersons() {
        List<PersonResponse> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons);
    }

    /** Get person by ID */
    @GetMapping("/{id}")
    public ResponseEntity<PersonResponse> getPersonById(@PathVariable String id) {
        PersonResponse response = personService.getPersonById(id);
        return ResponseEntity.ok(response);
    }

    /** Update person */
    @PutMapping("/{id}")
    public ResponseEntity<PersonResponse> updatePerson(
            @PathVariable String id,
            @Valid @RequestBody PersonRequest request) {
        PersonResponse response = personService.updatePerson(id, request);
        return ResponseEntity.ok(response);
    }

    /** Delete person */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }
}
