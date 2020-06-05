package spring.cloud.netflix.eureka.employee.statistic.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spring.cloud.netflix.eureka.employee.statistic.entities.CompanyEmployee;
import spring.cloud.netflix.eureka.employee.statistic.resources.CompanyEmployeeStatisticResource;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StatisticServiceTest {

    @MockBean
    private CompanyEmployeeStatisticResource resource;

    @Autowired
    private StatisticService statisticService;

    private CompanyEmployee employee1;
    private CompanyEmployee employee2;
    private List<CompanyEmployee> companyEmployees;

    @Before
    public void beforeClassMethod() {

        employee1 = new CompanyEmployee(
                1L,
                "FirstName_Test",
                "LastName_Test",
                "Department_Test",
                "Position_Test",
                LocalDate.of(2000, 1, 1),
                LocalDate.of(2020, 1, 1)
        );

        employee2 = new CompanyEmployee(
                2L,
                "FirstName_Test",
                "LastName_Test",
                "Department_Test",
                "Position_Test",
                LocalDate.of(2001, 2, 2),
                LocalDate.of(2021, 2, 2)
        );

        companyEmployees = Stream.of(employee1, employee2).collect(Collectors.toList());
    }

    @Test
    public void contextLoads() {
    }


    /**
     * Проверка среднего рабочего периода работы в компании с учетом возможных проблем с датами
     */
    @Test
    public void calculateAverageWorkPeriodInCompanyTest() {
        // Если все хорошо и нет проблем с датами

        long workDaysFirstEmployee = DAYS.between(employee1.getEmploymentDate(), employee1.getDismissalDate());
        long workDaysSecondEmployee = DAYS.between(employee2.getEmploymentDate(), employee2.getDismissalDate());
        double average = (workDaysFirstEmployee + workDaysSecondEmployee) / 2.0;

        double statisticServiceAverage = statisticService.calculateAverageWorkPeriodInCompany(companyEmployees);

        assertEquals(average, statisticServiceAverage, 0.0001);

        // Если сотрудник еще работает и отсутствует дата увольнения

        companyEmployees.get(0).setDismissalDate(null);
        employee1.setDismissalDate(null);

        workDaysFirstEmployee = DAYS.between(employee1.getEmploymentDate(), LocalDate.now());
        workDaysSecondEmployee = DAYS.between(employee2.getEmploymentDate(), employee2.getDismissalDate());
        average = (workDaysFirstEmployee + workDaysSecondEmployee) / 2.0;

        statisticServiceAverage = statisticService.calculateAverageWorkPeriodInCompany(companyEmployees);

        assertEquals(average, statisticServiceAverage, 0.0001);

        // Если отсутствует дата начала работы ---------------------

        companyEmployees.get(0).setEmploymentDate(null);

        average = workDaysSecondEmployee / 1.0;

        statisticServiceAverage = statisticService.calculateAverageWorkPeriodInCompany(companyEmployees);

        assertEquals(average, statisticServiceAverage, 0.0001);
    }

    @Test
    public void getDepartmentCountersTest() {
        companyEmployees.add(new CompanyEmployee(
                3L,
                companyEmployees.get(1).getFirstName(),
                companyEmployees.get(1).getLastName(),
                companyEmployees.get(1).getDepartment() + "_2",
                companyEmployees.get(1).getPosition(),
                companyEmployees.get(1).getEmploymentDate(),
                companyEmployees.get(1).getDismissalDate()
        ));

        Map<String, Long> result = new HashMap<>();
        result.put(companyEmployees.get(0).getDepartment(), 2L);
        result.put(companyEmployees.get(2).getDepartment(), 1L);

        Map<String, Long> statisticServiceResult = statisticService.getDepartmentCounters(companyEmployees);

        assertEquals(result, statisticServiceResult);
    }

    @Test
    public void getDepartmentWithLargestEmployeeNumberTest() {
        companyEmployees.add(new CompanyEmployee(
                3L,
                companyEmployees.get(1).getFirstName(),
                companyEmployees.get(1).getLastName(),
                companyEmployees.get(1).getDepartment() + "_2",
                companyEmployees.get(1).getPosition(),
                companyEmployees.get(1).getEmploymentDate(),
                companyEmployees.get(1).getDismissalDate()
        ));

        List<String> result = new ArrayList<>();
        result.add(companyEmployees.get(0).getDepartment());

        List<String> statisticServiceResult = statisticService.getDepartmentWithLargestEmployeeNumber(companyEmployees);

        assertEquals(result, statisticServiceResult);

        companyEmployees.get(1).setDepartment(null);
        result.clear();
        result.add(companyEmployees.get(0).getDepartment());
        result.add("NOT BE IN ANY DEPARTMENT");
        result.add(companyEmployees.get(2).getDepartment());

        statisticServiceResult = statisticService.getDepartmentWithLargestEmployeeNumber(companyEmployees);

        result.sort(String::compareTo);
        statisticServiceResult.sort(String::compareTo);

        assertEquals(result, statisticServiceResult);
    }
}
