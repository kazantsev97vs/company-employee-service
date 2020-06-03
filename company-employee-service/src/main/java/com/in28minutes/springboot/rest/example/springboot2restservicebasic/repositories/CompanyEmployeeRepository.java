package com.in28minutes.springboot.rest.example.springboot2restservicebasic.repositories;

import com.in28minutes.springboot.rest.example.springboot2restservicebasic.entities.CompanyEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * CRUD - репозиторий для управления сущностью "Сотрудник фирмы"
 */
@Repository
public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, Long> {
}