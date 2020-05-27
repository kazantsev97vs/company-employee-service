package com.in28minutes.springboot.rest.example.springboot2restservicebasic.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
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
    private Date employmentDate;

    /**
     * Дата увольнения
     */
    private Date dismissalDate;

}