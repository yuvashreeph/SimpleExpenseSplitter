package com.expense.splitter.service;

import com.expense.splitter.dto.PersonRequest;
import com.expense.splitter.dto.PersonResponse;
import com.expense.splitter.exception.ResourceNotFoundException;
import com.expense.splitter.model.Person;
import com.expense.splitter.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Person operations.
 * Handles creation, retrieval, update and deletion of Person records.
 */
@Service
public class PersonService {

    private final PersonRepository personRepository;

    /**
     * Manual constructor injection so this class works without Lombok.
     */
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Create a new person. Validates email uniqueness.
     */
    public PersonResponse createPerson(PersonRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PersonRequest must not be null");
        }

        if (personRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Person person = new Person();
        person.setName(request.getName());
        person.setEmail(request.getEmail());
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());

        Person saved = personRepository.save(person);
        return mapToResponse(saved);
    }

    /**
     * Return all persons as PersonResponse DTOs.
     */
    public List<PersonResponse> getAllPersons() {
        return personRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a person by id.
     */
    public PersonResponse getPersonById(String id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
        return mapToResponse(person);
    }

    /**
     * Update an existing person.
     */
    public PersonResponse updatePerson(String id, PersonRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("PersonRequest must not be null");
        }

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));

        // If email changed, ensure uniqueness
        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.equals(person.getEmail()) && personRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("Email already exists");
        }

        person.setName(request.getName());
        person.setEmail(newEmail);
        person.setUpdatedAt(LocalDateTime.now());

        Person updated = personRepository.save(person);
        return mapToResponse(updated);
    }

    /**
     * Delete a person by id.
     */
    public void deletePerson(String id) {
        if (!personRepository.existsById(id)) {
            throw new ResourceNotFoundException("Person not found with id: " + id);
        }
        personRepository.deleteById(id);
    }

    /**
     * Map Person entity to PersonResponse DTO.
     */
    private PersonResponse mapToResponse(Person person) {
        if (person == null) return null;

        return new PersonResponse(
                person.getId(),
                person.getName(),
                person.getEmail(),
                person.getCreatedAt(),
                person.getUpdatedAt()
        );
    }
}
