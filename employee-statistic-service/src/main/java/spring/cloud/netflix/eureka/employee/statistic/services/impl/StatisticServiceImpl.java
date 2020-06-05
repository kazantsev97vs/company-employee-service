package spring.cloud.netflix.eureka.employee.statistic.services.impl;

import org.springframework.stereotype.Service;
import spring.cloud.netflix.eureka.employee.statistic.entities.CompanyEmployee;
import spring.cloud.netflix.eureka.employee.statistic.services.StatisticService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
class StatisticServiceImpl implements StatisticService {

    @Override
    public Double calculateAverageWorkPeriodInCompany(List<CompanyEmployee> companyEmployees) {

        // массив -> преобразуем его в новый поток ->
        return companyEmployees.stream()
                // выбираем только теъ сотрудников, у которых установлена дата устройства на работу
                .filter(employee -> employee.getEmploymentDate() != null)
                // создаем поток, состоящий из количеств дней между устройством на работу и увольнением с неё
                // а для тех сотрудников, у которых отсутствует дата увольнения (они до сих пор работают), устанавливаем ей текущую дату
                .map(employee -> DAYS.between(employee.getEmploymentDate(), employee.getDismissalDate() == null ? LocalDate.now() : employee.getDismissalDate()))
                // возвращаем среднее
                .collect(Collectors.averagingLong(Long::longValue));
    }

    @Override
    public Map<String, Long> getDepartmentCounters(List<CompanyEmployee> companyEmployees) {

        // массив -> преобразуем его в поток ->
        return companyEmployees.stream()
                // у сотрудников у которых не указан отдел, указываем, что они не принадлежат к какому-либо отделу
                .peek(employee -> employee.setDepartment(employee.getDepartment() == null ? "NOT BE IN ANY DEPARTMENT" : employee.getDepartment()))
                // преобразуем поток в HashMap {key = department, value = List<CompanyEmployee>}
                .map(CompanyEmployee::getDepartment)
                // преобразуем поток отделов в HashMap {key = department, value = count}
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public List<String> getDepartmentWithLargestEmployeeNumber(List<CompanyEmployee> companyEmployees) {

        Map<String, Long> departmentCountersMap = getDepartmentCounters (companyEmployees);
        Long maxCount = departmentCountersMap.values().stream().max(Long::compareTo).orElse(0L);

        return departmentCountersMap.entrySet().stream().filter(stringLongEntry -> maxCount.equals(stringLongEntry.getValue()))
                .map(Map.Entry::getKey).collect(Collectors.toList());
    }
}