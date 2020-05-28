package com.in28minutes.springboot.rest.example.springboot2restservicebasic.resources;

import com.in28minutes.springboot.rest.example.springboot2restservicebasic.entities.CompanyEmployee;
import com.in28minutes.springboot.rest.example.springboot2restservicebasic.exceptions.CompanyEmployeeNotFoundException;
import com.in28minutes.springboot.rest.example.springboot2restservicebasic.repositories.CompanyEmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.Optional;

@RestController // Комбинация @Controller и @ResponseBody. Возвращаемые компоненты преобразуются в / из JSON / XML.
@AllArgsConstructor
public class CompanyEmployeeResource {

    private final CompanyEmployeeRepository companyEmployeeRepository;


    // CREATE --------------------------------------------------------

    @PostMapping("/company-employees")
    public ResponseEntity<Object> createCompanyEmployee(@RequestBody CompanyEmployee companyEmployee) {

        CompanyEmployee savedCompanyEmployee = companyEmployeeRepository.save(companyEmployee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedCompanyEmployee.getId()).toUri();

        return ResponseEntity.created(location).build();
    }


    // READ ----------------------------------------------------------

    @GetMapping("/company-employees")
    public Page<CompanyEmployee> retrieveAllCompanyEmployeesPageByPage(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) Sort.Direction direction,
            @RequestParam(required = false) String... properties
    ) {
        Pageable pageable = PageRequest.of(page-1, size);

        if (direction != null && properties != null && properties.length > 0) {
            pageable = PageRequest.of(page-1, size, direction, properties);
        }

        return companyEmployeeRepository.findAll(pageable);
    }

    @GetMapping("/company-employees/{id}")
    public CompanyEmployee retrieveCompanyEmployee(@PathVariable long id) throws CompanyEmployeeNotFoundException {
        Optional<CompanyEmployee> companyEmployee = companyEmployeeRepository.findById(id);

        if (companyEmployee.isEmpty()) {
            throw new CompanyEmployeeNotFoundException("id-" + id);
        }

        return companyEmployee.get();
    }


    // UPDATE --------------------------------------------------------

    @PutMapping("/company-employees/{id}")
    public ResponseEntity<Object> updateCompanyEmployee(@RequestBody CompanyEmployee companyEmployee, @PathVariable long id) {

        if (companyEmployeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        companyEmployee.setId(id);

        companyEmployeeRepository.save(companyEmployee);

        return ResponseEntity.noContent().build();
    }


    // DELETE --------------------------------------------------------

    @DeleteMapping("/company-employees/{id}")
    public void deleteCompanyEmployee(@PathVariable long id) {
        companyEmployeeRepository.deleteById(id);
    }
}