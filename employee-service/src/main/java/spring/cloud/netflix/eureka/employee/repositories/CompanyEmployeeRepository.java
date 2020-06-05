package spring.cloud.netflix.eureka.employee.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.cloud.netflix.eureka.employee.entities.CompanyEmployee;

/**
 * CRUD - репозиторий для управления сущностью "Сотрудник фирмы"
 */
@Repository
public interface CompanyEmployeeRepository extends JpaRepository<CompanyEmployee, Long> {
}