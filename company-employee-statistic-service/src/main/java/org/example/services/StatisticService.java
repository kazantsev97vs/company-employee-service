package org.example.services;

import org.example.entities.CompanyEmployee;

import java.util.List;
import java.util.Map;

public interface StatisticService {

    Double calculateAverageWorkPeriodInCompany(CompanyEmployee[] companyEmployees);

    Map<String, Long> getDepartmentCounters(CompanyEmployee[] companyEmployees);

    List<String> getDepartmentWithLargestEmployeeNumber(CompanyEmployee[] companyEmployees);
}
