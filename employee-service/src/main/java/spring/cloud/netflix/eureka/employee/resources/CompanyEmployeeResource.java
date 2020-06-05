package spring.cloud.netflix.eureka.employee.resources;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.cloud.netflix.eureka.employee.entities.CompanyEmployee;
import spring.cloud.netflix.eureka.employee.exceptions.ResourceNotFoundException;
import spring.cloud.netflix.eureka.employee.repositories.CompanyEmployeeRepository;

import java.util.List;

/**
 * CRUD REST-API для управления сущностью "Сотрудник фирмы" в базе данных.
 */
@RequestMapping("/company-employees")
@RestController // Комбинация @Controller и @ResponseBody. Возвращаемые компоненты преобразуются в / из JSON / XML.
@AllArgsConstructor
public class CompanyEmployeeResource {

    private final CompanyEmployeeRepository companyEmployeeRepository;


    // CREATE --------------------------------------------------------

    /**
     * Сохранение сущности предоставляемого сотрудника фирмы в БД
     * @param companyEmployee - предоставленный объект сотрудника
     * @return сохраненную сущность сотрудника фирмы
     */
    @PostMapping
    public ResponseEntity<CompanyEmployee> createCompanyEmployee(
            @RequestBody CompanyEmployee companyEmployee
    ) {
        CompanyEmployee savedCompanyEmployee = companyEmployeeRepository.save(companyEmployee);

        return new ResponseEntity<>(
                savedCompanyEmployee,
                HttpStatus.CREATED
        );
    }


    // READ ----------------------------------------------------------

    /**
     * Получить список всех сущностей сотрудников фирмы
     * @return все сущности сотрудников фирмы из БД
     */
    @GetMapping("/all")
    public ResponseEntity<List<CompanyEmployee>> retrieveAllCompanyEmployees() {
        return new ResponseEntity<>(
                companyEmployeeRepository.findAll(),
                HttpStatus.OK
        );
    }

    /**
     * Предоставление списка из сущностей сотрудников фирмы постранично
     * @param page - текущая страниа
     * @param size - количество сущностей, которое следует отображать на странице
     * @param direction - Для каждого столбца указывается порядок сортировки записей:
     *                  "ASC" - сортировать записи по возрастанию (по умолчанию),
     *                  "DESC" - по убыванию
     * @param properties - свойства сущности, по которым осуществляется сортировка в указанном выше "direction"
     * @return Объект страницы, содержащий отсортированный и ограниченный по количеству список сотрудников фирмы
     */
    @GetMapping
    public ResponseEntity<Page<CompanyEmployee>> retrieveAllCompanyEmployeesPageByPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction,
            @RequestParam(required = false, defaultValue = "firstName") String... properties
    ) {
        Pageable pageable = PageRequest.of(page - 1, size, direction, properties);

        return new ResponseEntity<>(
                companyEmployeeRepository.findAll(pageable),
                HttpStatus.OK
        );
    }

    /**
     * Извлечение сущности сотрудника фирмы по его идентификатору из БД
     * @param id - идентификатор
     * @return сущность сотрудника фирмы
     * @throws ResourceNotFoundException - Не получилось найти сотрудника фирмы по указанному идентификатору
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompanyEmployee> retrieveCompanyEmployee(
            @PathVariable(value = "id") long id
    ) throws ResourceNotFoundException {

        CompanyEmployee employee = companyEmployeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CompanyEmployee.class, id));

        return new ResponseEntity<>(
                employee,
                HttpStatus.OK
        );
    }


    // UPDATE --------------------------------------------------------

    /**
     * Обновление сущности сотрудника фирмы по его идентификатору
     * @param id - идентификатор
     * @param companyEmployee - обновленная версия сущности сотрудника фирмы
     * @return обновленную версию сущности сотрудника фирмы
     * @throws ResourceNotFoundException - Не получилось найти сотрудника фирмы по указанному идентификатору
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompanyEmployee> updateCompanyEmployee(
            @PathVariable long id,
            @RequestBody  CompanyEmployee companyEmployee
    ) throws ResourceNotFoundException {

        if (!companyEmployeeRepository.existsById(id))
            throw new ResourceNotFoundException(CompanyEmployee.class, id);

        companyEmployee.setId(id);

        return new ResponseEntity<>(
                companyEmployeeRepository.save(companyEmployee),
                HttpStatus.OK
        );
    }


    // DELETE --------------------------------------------------------

    /**
     * Удаление сущности сотрудника фирмы по его идентификатору
     * @param id - идентификатор
     * @return булевый идентификатор выполнения операции
     * @throws ResourceNotFoundException - Не получилось найти сотрудника фирмы по указанному идентификатору
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteCompanyEmployee(
            @PathVariable long id
    ) throws ResourceNotFoundException {

        if (!companyEmployeeRepository.existsById(id))
            throw new ResourceNotFoundException(CompanyEmployee.class, id);

        companyEmployeeRepository.deleteById(id);

        return new ResponseEntity<>(
                true,
                HttpStatus.OK
        );
    }
}