package spring.cloud.netflix.eureka.employee.statistic.resources;

import lombok.AllArgsConstructor;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import spring.cloud.netflix.eureka.employee.statistic.entities.CompanyEmployee;
import spring.cloud.netflix.eureka.employee.statistic.feign_clients.EmployeeFeignClient;
import spring.cloud.netflix.eureka.employee.statistic.services.StatisticService;

import java.util.List;

/**
 * REST-API для получения статистики над сотрудниками фирмы сервиса "employee-service".
 */
@RequestMapping("/company-employees-statistic")
@RestController // Комбинация @Controller и @ResponseBody. Возвращаемые компоненты преобразуются в / из JSON / XML.
@AllArgsConstructor
public class CompanyEmployeeStatisticResource {

    private final StatisticService statisticService;

    private final EmployeeFeignClient employeeFeignClient;


    // STATISTICS ----------------------------------------------------

    /**
     * Вычислить средний период работы в компании сотрудников
     * сумма количеств дней работы каждого поделить на число сотрудников
     *
     * @return средний период работы в компании
     */
    @GetMapping("/average-work-period")
    public ResponseEntity<Double> calculateAverageWorkPeriodInCompany() {
        List<CompanyEmployee> companyEmployees = employeeFeignClient.retrieveAllCompanyEmployees().getBody();

        return new ResponseEntity<>(
                statisticService.calculateAverageWorkPeriodInCompany(companyEmployees),
                HttpStatus.OK
        );
    }

    /**
     * Получить список отделов с наибольшим числом сотрудников
     *
     * @return список названий отделов
     */
    @GetMapping("/largest-employee-number-department")
    public ResponseEntity<List<String>> getDepartmentWithLargestEmployeeNumber() {
        List<CompanyEmployee> companyEmployees = employeeFeignClient.retrieveAllCompanyEmployees().getBody();

        return new ResponseEntity<>(
                statisticService.getDepartmentWithLargestEmployeeNumber(companyEmployees),
                HttpStatus.OK
        );
    }
}