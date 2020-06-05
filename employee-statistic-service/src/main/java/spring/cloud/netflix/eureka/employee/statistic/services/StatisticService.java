package spring.cloud.netflix.eureka.employee.statistic.services;

import spring.cloud.netflix.eureka.employee.statistic.entities.CompanyEmployee;

import java.util.List;
import java.util.Map;

public interface StatisticService {

    Double calculateAverageWorkPeriodInCompany(List<CompanyEmployee> companyEmployees);

    Map<String, Long> getDepartmentCounters(List<CompanyEmployee> companyEmployees);

    List<String> getDepartmentWithLargestEmployeeNumber(List<CompanyEmployee> companyEmployees);
}
