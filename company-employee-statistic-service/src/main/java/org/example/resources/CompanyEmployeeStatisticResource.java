package org.example.resources;

import lombok.AllArgsConstructor;
import org.example.entities.CompanyEmployee;
import org.example.services.StatisticService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 *
 */
@RequestMapping("/company-employees-statistic")
@RestController // Комбинация @Controller и @ResponseBody. Возвращаемые компоненты преобразуются в / из JSON / XML.
@AllArgsConstructor
public class CompanyEmployeeStatisticResource {

    final RestTemplate restTemplate = new RestTemplate();

    final String companyEmployeesUrl = "http://localhost:8080/company-employees/";

    final StatisticService statisticService;

    // STATISTICS ----------------------------------------------------

    /**
     * Вычислить средний период работы в компании
     * сумма количеств дней работы каждого поделить на число сотрудников
     * @return период
     */
    @GetMapping("/average-work-period")
    public ResponseEntity<Double> calculateAverageWorkPeriodInCompany() {
        CompanyEmployee[] companyEmployees = restTemplate.getForObject(companyEmployeesUrl + "/all", CompanyEmployee[].class);

        return new ResponseEntity<>(
                statisticService.calculateAverageWorkPeriodInCompany(companyEmployees),
                HttpStatus.OK
        );
    }

    /**
     *
     * @return
     */
    @GetMapping("/largest-employee-number-department")
    public ResponseEntity<List<String>> getDepartmentWithLargestEmployeeNumber() {
        CompanyEmployee[] companyEmployees = restTemplate.getForObject(companyEmployeesUrl + "/all", CompanyEmployee[].class);

        return new ResponseEntity<>(
                statisticService.getDepartmentWithLargestEmployeeNumber(companyEmployees),
                HttpStatus.OK
        );
    }
}