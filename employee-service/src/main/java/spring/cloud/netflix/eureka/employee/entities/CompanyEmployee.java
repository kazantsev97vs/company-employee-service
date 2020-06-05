package spring.cloud.netflix.eureka.employee.entities;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDate;

/**
 * Сущность "Сотрудник фирмы"
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompanyEmployee {

    @Id             // Первичный ключ объекта
    @GeneratedValue // Спецификация стратегий генерации значений первичных ключей
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