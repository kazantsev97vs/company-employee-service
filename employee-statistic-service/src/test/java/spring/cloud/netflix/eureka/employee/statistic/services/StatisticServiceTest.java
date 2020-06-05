package spring.cloud.netflix.eureka.employee.statistic.services;

import org.junit.BeforeClass;
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

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class StatisticServiceTest {

    @MockBean
    private CompanyEmployeeStatisticResource resource;

    @Autowired
    private StatisticService statisticService;

//    private static List<CompanyEmployee> companyEmployees;
    private static final CompanyEmployee[] companyEmployees = new CompanyEmployee[2];

    @BeforeClass
    public static void beforeClassMethod() {

//        CompanyEmployee companyEmployee1 = new CompanyEmployee(
//                1L,
//                "FirstName_Test",
//                "LastName_Test",
//                "Department_Test",
//                "Position_Test",
//                LocalDate.of(2000, 1, 1),
//                LocalDate.of(2020, 1, 1)
//        );
//
//        CompanyEmployee companyEmployee2 = new CompanyEmployee(
//                2L,
//                "FirstName_Test",
//                "LastName_Test",
//                "Department_Test",
//                "Position_Test",
//                LocalDate.of(2001, 2, 2),
//                LocalDate.of(2021, 2, 2)
//        );
//
//        companyEmployees = Stream.of(companyEmployee1, companyEmployee2).collect(Collectors.toList());

        for (int i = 0; i < companyEmployees.length; i++) {
            // Создаю тестовых сотрудников фирмы..
            companyEmployees[i] = new CompanyEmployee(
                    i + 1L,
                    "FirstName_Test",
                    "LastName_Test",
                    "Department_Test",
                    "Position_Test",
                    LocalDate.of(2000 + i, i + 1, i + 1),
                    LocalDate.of(2020 + i, i + 1, i + 1)
            );
        }
    }

    @Test
    public void contextLoads() {
    }


    /**
     * Проверка среднего рабочего периода работы в компании с учетом возможных проблем с датами
     */
    @Test
    public void calculateAverageWorkPeriodInCompanyTest() {
        CompanyEmployee employee1 = companyEmployees[0];
        CompanyEmployee employee2 = companyEmployees[1];

        // Если все хорошо и нет проблем с датами

        long workDaysFirstEmployee = DAYS.between(employee1.getEmploymentDate(), employee1.getDismissalDate());
        long workDaysSecondEmployee = DAYS.between(employee2.getEmploymentDate(), employee2.getDismissalDate());
        double average = (workDaysFirstEmployee + workDaysSecondEmployee) / 2.0;

        double statisticServiceAverage = statisticService.calculateAverageWorkPeriodInCompany(companyEmployees);

        assertEquals(average, statisticServiceAverage, 0.0001);

        // Если сотрудник еще работает и отсутствует дата увольнения

        companyEmployees[0].setDismissalDate(null);
        employee1 = companyEmployees[0];

        workDaysFirstEmployee = DAYS.between(employee1.getEmploymentDate(), LocalDate.now());
        workDaysSecondEmployee = DAYS.between(employee2.getEmploymentDate(), employee2.getDismissalDate());
        average = (workDaysFirstEmployee + workDaysSecondEmployee) / 2.0;

        statisticServiceAverage = statisticService.calculateAverageWorkPeriodInCompany(companyEmployees);

        assertEquals(average, statisticServiceAverage, 0.0001);

        // Если отсутствует дата начала работы ---------------------

        companyEmployees[0].setEmploymentDate(null);

        average = workDaysSecondEmployee / 1.0;

        statisticServiceAverage = statisticService.calculateAverageWorkPeriodInCompany(companyEmployees);

        assertEquals(average, statisticServiceAverage, 0.0001);
    }

    @Test
    public void getDepartmentCountersTest() {
        CompanyEmployee[] companyEmployeesTest = new CompanyEmployee[3];
        companyEmployeesTest[0] = companyEmployees[0];
        companyEmployeesTest[1] = companyEmployees[1];
        companyEmployeesTest[2] = new CompanyEmployee(
                3L,
                companyEmployeesTest[1].getFirstName(),
                companyEmployeesTest[1].getLastName(),
                companyEmployeesTest[1].getDepartment() + "_2",
                companyEmployeesTest[1].getPosition(),
                companyEmployeesTest[1].getEmploymentDate(),
                companyEmployeesTest[1].getDismissalDate()
        );

        Map<String, Long> result = new HashMap<>();
        result.put(companyEmployeesTest[0].getDepartment(), 2L);
        result.put(companyEmployeesTest[2].getDepartment(), 1L);

        Map<String, Long> statisticServiceResult = statisticService.getDepartmentCounters(companyEmployeesTest);

        assertEquals(result, statisticServiceResult);
    }

    @Test
    public void getDepartmentWithLargestEmployeeNumberTest() {
        CompanyEmployee[] companyEmployeesTest = new CompanyEmployee[3];
        companyEmployeesTest[0] = companyEmployees[0];
        companyEmployeesTest[1] = companyEmployees[1];
        companyEmployeesTest[2] = new CompanyEmployee(
                3L,
                companyEmployeesTest[1].getFirstName(),
                companyEmployeesTest[1].getLastName(),
                companyEmployeesTest[1].getDepartment() + "_2",
                companyEmployeesTest[1].getPosition(),
                companyEmployeesTest[1].getEmploymentDate(),
                companyEmployeesTest[1].getDismissalDate()
        );

        List<String> result = new ArrayList<>();
        result.add(companyEmployeesTest[0].getDepartment());

        List<String> statisticServiceResult = statisticService.getDepartmentWithLargestEmployeeNumber(companyEmployeesTest);

        assertEquals(result, statisticServiceResult);

        companyEmployeesTest[1].setDepartment(null);
        result.clear();
        result.add(companyEmployeesTest[0].getDepartment());
        result.add("NOT BE IN ANY DEPARTMENT");
        result.add(companyEmployeesTest[2].getDepartment());

        statisticServiceResult = statisticService.getDepartmentWithLargestEmployeeNumber(companyEmployeesTest);

        result.sort(String::compareTo);
        statisticServiceResult.sort(String::compareTo);

        assertEquals(result, statisticServiceResult);
    }
}
