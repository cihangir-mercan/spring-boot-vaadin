package com.cihangirmercan;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete
public interface PersonRepository extends JpaRepository<Person, Long> {
	List<Person> findByNameStartsWithIgnoreCase(String filterText);
}