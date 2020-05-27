package com.in28minutes.springboot.rest.example.springboot2restservicebasic.resources;

import com.in28minutes.springboot.rest.example.springboot2restservicebasic.entities.CompanyEmployee;
import com.in28minutes.springboot.rest.example.springboot2restservicebasic.exceptions.CompanyEmployeeNotFoundException;
import com.in28minutes.springboot.rest.example.springboot2restservicebasic.repositories.CompanyEmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController // Комбинация @Controller и @ResponseBody. Возвращаемые компоненты преобразуются в / из JSON / XML.
@AllArgsConstructor
public class CompanyEmployeeResource {

    private final CompanyEmployeeRepository companyEmployeeRepository;


    @PostMapping("/company-employees")
    public ResponseEntity<Object> createCompanyEmployee(@RequestBody CompanyEmployee companyEmployee) {

        CompanyEmployee savedCompanyEmployee = companyEmployeeRepository.save(companyEmployee);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedCompanyEmployee.getId()).toUri();

        return ResponseEntity.created(location).build();
    }


    @GetMapping("/company-employees")
    public List<CompanyEmployee> retrieveAllCompanyEmployees() {
        return companyEmployeeRepository.findAll();
    }

    @GetMapping("/company-employees/{id}")
    public CompanyEmployee retrieveCompanyEmployee(@PathVariable long id) throws CompanyEmployeeNotFoundException {
        Optional<CompanyEmployee> companyEmployee = companyEmployeeRepository.findById(id);

        if (!companyEmployee.isPresent()) {
            throw new CompanyEmployeeNotFoundException("id-" + id);
        }

        return companyEmployee.get();
    }


    @PutMapping("/company-employees/{id}")
    public ResponseEntity<Object> updateCompanyEmployee(@RequestBody CompanyEmployee companyEmployee, @PathVariable long id) {

        if (companyEmployeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        companyEmployee.setId(id);

        companyEmployeeRepository.save(companyEmployee);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/company-employees/{id}")
    public void deleteCompanyEmployee(@PathVariable long id) {
        companyEmployeeRepository.deleteById(id);
    }
}