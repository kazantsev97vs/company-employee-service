package spring.cloud.netflix.eureka.employee.resources;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import spring.cloud.netflix.eureka.employee.SpringBoot2RestServiceBasicApplication;
import spring.cloud.netflix.eureka.employee.entities.CompanyEmployee;
import spring.cloud.netflix.eureka.employee.repositories.CompanyEmployeeRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringBoot2RestServiceBasicApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompanyEmployeeResourceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompanyEmployeeRepository companyEmployeeRepository;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port + "/company-employees";
    }

    @Test
    public void contextLoads() {}

    private static final Logger log = Logger.getLogger(CompanyEmployeeResourceIntegrationTest.class);

    private static CompanyEmployee employee;

    @BeforeClass
    public static void beforeClassMethod() {
        // Создаю тестового сотрудника фирмы..
        employee = new CompanyEmployee();
        employee.setFirstName("FirstName_Test");
        employee.setLastName("LastName_Test");
        employee.setDepartment("Department_Test");
        employee.setPosition("Position_Test");
        employee.setEmploymentDate(LocalDate.now());
        employee.setDismissalDate(LocalDate.now());
    }


    // CREATING TESTS --------------------------------------------------------

    /**
     * Проверка того, что сотрудник компании должен существовать в базе данных после его создания
     */
    @Test
    public void companyEmployeeEntityShouldExistsInDatabaseAfterItsCreating() {
        log.info("Тест 'CompanyEmployeeEntityShouldExistsInDatabaseAfterItsCreating' начал исполнение..");

        log.info(String.format("Выполняю POST-запрос с передачей созданного тестовый объекта на %s ресурс..", getRootUrl()));
        CompanyEmployee receivedCompanyEmployeeFromResponse = restTemplate.postForObject(
                getRootUrl(),
                employee,
                CompanyEmployee.class
        );

        log.info("Проверяю, что метод createCompanyEmployee() вернул созданный объект сущности..");
        assertNotNull(
                "Ресурс не вернул объект сущности..",
                receivedCompanyEmployeeFromResponse
        );

        // Присваиваю идентификатор сущности из ответа для последующего сравнивания объектов
        employee.setId(receivedCompanyEmployeeFromResponse.getId());

        log.info("Сверяю отправленный объект с вернувшимся в ответе объектом..");
        assertEquals(
                "Объекты не совпали..",
                receivedCompanyEmployeeFromResponse,
                employee
        );

        log.info("Достаю сущность из БД и сравниваю её с созданной сущностью сотрудника фирмы..");
        assertEquals(
                "Объекты не совпали..",
                companyEmployeeRepository.findById(receivedCompanyEmployeeFromResponse.getId())
                        .orElse(new CompanyEmployee()),
                receivedCompanyEmployeeFromResponse
        );

        log.info("Удаляю из БД созданную сущность..");
        companyEmployeeRepository.deleteById(employee.getId());
    }


    // READING TESTS -----------------------------------------------------------

    /**
     * Проверка возвращения списка объектов сущностей сотрудников фирмы постранично
     */
    @Test
    public void companyEmployeeEntityListShouldBeReturnedByPartsPageByPage() {
        log.info("Тест 'companyEmployeeEntityListShouldBeReturnedByPartsPageByPage' начал исполнение..");

        List<CompanyEmployee> companyEmployeeList = new ArrayList<>();

        log.info("Сохраняю 10 тестовых объектов в БД..");
        for (int i = 0; i < 10; i++) companyEmployeeList.add(
                companyEmployeeRepository.save(new CompanyEmployee(
                        null,
                        employee.getFirstName() + "_" + i,
                        employee.getLastName() + "_" + i,
                        employee.getDepartment() + "_" + i,
                        employee.getPosition() + "_" + i,
                        LocalDate.now(),
                        LocalDate.now()
                        )
                )
        );

        final int page = 1;
        final int size = 5;
        final Sort.Direction direction = Sort.Direction.ASC;
        final String properties = "firstName";
        final String url = getRootUrl() + "?page=%d&size=%d&direction=%s&properties=%s";
        final String formatFirstPage = String.format(url, page, size, direction, properties);
        final String formatSecondPage = String.format(url, page + 1, size, direction, properties);
        final Pageable pageRequestFirst = PageRequest.of(page - 1, size, direction, properties);
        final Pageable pageRequestSecond = PageRequest.of(page, size, direction, properties);

        log.info("Выполняю запрос к БД для получения страницы сотрудников компании, используя подготовленный 'Pageable-объект'..");
        Page<CompanyEmployee> companyEmployeePage = companyEmployeeRepository.findAll(pageRequestFirst);
        List<CompanyEmployee> foundedCompanyEmployeeListByPageable = companyEmployeePage.getContent();

        log.info(String.format("Выполняю GET-запрос с передачей тех же параметров на %s ресурс..", formatFirstPage));
        Map<?, ?> pageMap = restTemplate.getForObject(
                formatFirstPage,
                Map.class
        );

        List<?> contentMap = (List<?>) pageMap.get("content");
        List<CompanyEmployee> returnedCompanyEmployeeList = new ArrayList<>();

        for (Object o : contentMap) returnedCompanyEmployeeList.add(convertMapToCompanyEmployeeObject((Map<?, ?>) o));

        log.info("Сравниваю списки сотрудников компании полученные из репозитория и ответом на запрос..");
        assertEquals(returnedCompanyEmployeeList, foundedCompanyEmployeeListByPageable);

        log.info("Выполняю запрос к БД для получения следующей страницы сотрудников компании, используя новый 'Pageable-объект'..");
        Page<CompanyEmployee> companyEmployeePage2 = companyEmployeeRepository.findAll(pageRequestSecond);
        List<CompanyEmployee> foundedCompanyEmployeeListByPageable2 = companyEmployeePage2.getContent();

        log.info(String.format("Выполняю GET-запрос с передачей новых параметров на %s ресурс..", formatSecondPage));
        Map<?, ?> pageMap2 = restTemplate.getForObject(
                formatSecondPage,
                Map.class
        );

        List<?> contentMap2 = (List<?>) pageMap2.get("content");

        List<CompanyEmployee> returnedCompanyEmployeeList2 = new ArrayList<>();

        for (Object o : contentMap2) returnedCompanyEmployeeList2.add(convertMapToCompanyEmployeeObject((Map<?, ?>) o));

        log.info("Сравниваю списки сотрудников компании полученные из репозитория и ответом на запрос..");
        assertEquals(
                "Списки не совпали..",
                returnedCompanyEmployeeList2,
                foundedCompanyEmployeeListByPageable2
        );

        log.info("Сравниваю списки сотрудников компании полученные из репозитория первым запросом и вторым..");
        assertNotEquals(
                "Списки совпали => разные запросы дали один и тот же результат..",
                returnedCompanyEmployeeList,
                returnedCompanyEmployeeList2
        );

        log.info("Сравниваю списки сотрудников компании полученные ответом на первый запрос и второй..");
        assertNotEquals(
                "Списки совпали => разные запросы дали один и тот же результат..",
                foundedCompanyEmployeeListByPageable,
                foundedCompanyEmployeeListByPageable2
        );

        log.info("Удаляю из БД сохраненные тестовые объекты..");
        companyEmployeeRepository.deleteAll(companyEmployeeList);
    }

    /**
     * Проверка возвращения объекта сущности сотрудника фирмы по его идентификатору (при её наличии и отсутствии)
     */
    @Test
    public void companyEmployeeEntityShouldBeReturnedIfItsExistsInDatabaseAfterItsRetrievingById() {
        log.info("Тест 'companyEmployeeEntityShouldBeReturnedIfItsExistsInDatabaseAfterItsRetrievingById' начал исполнение..");

        log.info("Сохраняю тестовый объект в БД..");
        CompanyEmployee savedCompanyEmployee = companyEmployeeRepository.save(employee);

        log.info(String.format("Выполняю GET-запрос на %s/%s ресурс..", getRootUrl(), savedCompanyEmployee.getId()));
        CompanyEmployee receivedCompanyEmployeeFromResponse = restTemplate.getForObject(
                getRootUrl() + "/" + savedCompanyEmployee.getId(),
                CompanyEmployee.class
        );

        log.info("Проверяю, что метод retrieveCompanyEmployee() вернул объект сущности..");
        assertNotNull(
                "Ресурс не вернул объект сущности..",
                receivedCompanyEmployeeFromResponse
        );

        log.info("Сверяю сохраненный тестовый объект в БД с вернувшимся в ответе объектом..");
        assertEquals(
                "Объекты не совпали..",
                savedCompanyEmployee,
                receivedCompanyEmployeeFromResponse
        );

        log.info("Удаляю из БД сохраненный тестовый объект..");
        companyEmployeeRepository.deleteById(savedCompanyEmployee.getId());

        log.info("Пытаюсь получить не существующую в БД сущность..");
        Map<String, ?> errorResponse = restTemplate.getForObject(
                getRootUrl() + "/" + receivedCompanyEmployeeFromResponse.getId(),
                Map.class
        );

        log.info("Проверяю выбрасывание исключения 'ResourceNotFoundException'..");
        assertEquals(errorResponse.get("error"), "Not Found");
    }


    // UPDATING TESTS --------------------------------------------------------

    /**
     * Проверка того, что сущность в БД изменяется после вызова метода по изменению сущности
     */
    @Test
    public void companyEmployeeEntityShouldBeChangedInDatabaseAfterItsUpdating() {
        log.info("Тест 'companyEmployeeEntityShouldBeChangedInDatabaseAfterItsUpdating' начал исполнение..");

        log.info("Сохраняю тестовый объект в БД..");
        CompanyEmployee savedCompanyEmployee = companyEmployeeRepository.save(employee);

        log.info("Изменяю объект сущности");
        savedCompanyEmployee.setFirstName(savedCompanyEmployee.getFirstName() + "_Updated");

        log.info(String.format("Выполняю PUT-запрос на %s/%s ресурс..", getRootUrl(), savedCompanyEmployee.getId()));
        ResponseEntity<CompanyEmployee> responseEntity = restTemplate.exchange(
                getRootUrl() + "/" + savedCompanyEmployee.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(savedCompanyEmployee, new HttpHeaders()),
                CompanyEmployee.class
        );

        log.info("Проверяю, что метод updateCompanyEmployee() вернул объект сущности..");
        assertNotNull(
                "Ресурс не вернул объект сущности..",
                responseEntity.getBody()
        );

        log.info("Достаю из БД объект сущности и сверяю его с вернувшимся в ответе объектом..");
        assertEquals(
                "Объекты не совпали..",
                responseEntity.getBody(),
                companyEmployeeRepository.findById(savedCompanyEmployee.getId()).orElse(null)
        );

        log.info("Удаляю из БД сохраненный тестовый объект..");
        companyEmployeeRepository.deleteById(savedCompanyEmployee.getId());
    }


    // DELETING TESTS --------------------------------------------------------

    /**
     * Проверка удаления сущности из БД после вызова метода по удалению объекта
     */
    @Test
    public void companyEmployeeEntityShouldBeMissedFromDatabaseAfterItsDeleting() {
        log.info("Тест 'companyEmployeeEntityShouldBeMissedFromDatabaseAfterItsDeleting' начал исполнение..");

        log.info("Сохраняю тестовый объект в БД..");
        CompanyEmployee savedCompanyEmployee = companyEmployeeRepository.save(employee);

        log.info(String.format("Выполняю DELETE-запрос на %s/%s ресурс..", getRootUrl(), savedCompanyEmployee.getId()));
        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                getRootUrl() + "/" + savedCompanyEmployee.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(employee, new HttpHeaders()),
                Boolean.class
        );

        log.info("Проверяю, что метод deleteCompanyEmployee() вернул объект сущности..");
        assertNotNull(
                "Ресурс не вернул объект сущности..",
                responseEntity.getBody()
        );

        log.info("Проверяю, что метод deleteCompanyEmployee() вернул булевый положительный ответ..");
        assertTrue(
                "Ресурс не вернул булевый положительный ответ..",
                responseEntity.getBody()
        );

        log.info("Проверяю, что объекта больше нет в БД..");
        assertNull(
                "Объект не был удален",
                companyEmployeeRepository.findById(savedCompanyEmployee.getId()).orElse(null)
        );
    }


    // TEST HELP-METHODS -----------------------------------------------------

    /**
     * Получить объект "Сотрудника фирмы" из объекта, реализующего интерфейс Map
     * @param companyEmployeeMap - объект, реализующий интерфейс Map
     * @return CompanyEmployee объект
     */
    public CompanyEmployee convertMapToCompanyEmployeeObject(Map<?, ?> companyEmployeeMap) {
        String[] employmentDateArray = companyEmployeeMap.get("employmentDate").toString().split("-");
        String[] dismissalDateArray = companyEmployeeMap.get("dismissalDate").toString().split("-");

        return new CompanyEmployee(
                Long.valueOf((Integer) companyEmployeeMap.get("id")),
                companyEmployeeMap.get("firstName").toString(),
                companyEmployeeMap.get("lastName").toString(),
                companyEmployeeMap.get("department").toString(),
                companyEmployeeMap.get("position").toString(),
                LocalDate.of(Integer.parseInt(employmentDateArray[0]), Integer.parseInt(employmentDateArray[1]), Integer.parseInt(employmentDateArray[2])),
                LocalDate.of(Integer.parseInt(dismissalDateArray[0]), Integer.parseInt(dismissalDateArray[1]), Integer.parseInt(dismissalDateArray[2]))
        );
    }
}
