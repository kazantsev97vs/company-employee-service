package spring.cloud.netflix.eureka.employee.statistic.feign_clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import spring.cloud.netflix.eureka.employee.statistic.entities.CompanyEmployee;

import java.util.List;

@FeignClient(name = "employee-service")
public interface EmployeeFeignClient {

    @GetMapping("/company-employees/all")
    ResponseEntity<List<CompanyEmployee>> retrieveAllCompanyEmployees();
}