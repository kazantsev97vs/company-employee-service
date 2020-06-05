package spring.cloud.netflix.eureka.employee.statistic.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Сущность "Сотрудник фирмы"
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyEmployee implements Serializable {

    private Long id;

    /**
     * Имя
     */
    private String firstName;

    /**
     * Фамилия
     */
    private String lastName;

    /**
     * Отдел
     */
    private String department;

    /**
     * Должность
     */
    private String position;

    /**
     * Дата принятия на работу
     */
    private LocalDate employmentDate;

    /**
     * Дата увольнения
     */
    private LocalDate dismissalDate;

}