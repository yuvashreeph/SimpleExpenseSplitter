// src/services/person.service.js

import apiClient from './api.service';

class PersonService {
    getAll() {
        // Uses the apiClient, which automatically adds the JWT header
        return apiClient.get('/persons');
    }

    create(person) {
        return apiClient.post('/persons', person);
    }

    update(id, person) {
        return apiClient.put(`/persons/${id}`, person);
    }

    delete(id) {
        return apiClient.delete(`/persons/${id}`);
    }
}

export default new PersonService();