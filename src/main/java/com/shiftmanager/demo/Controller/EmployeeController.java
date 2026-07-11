package com.shiftmanager.demo.Controller;

import com.shiftmanager.demo.Entities.Employee;
import com.shiftmanager.demo.Service.EmployeeService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    @PostMapping("/setupDummyEmployees")
    public ResponseEntity<String> addDummyEmployes(){
        try {
            employeeService.createAndAddEmployees();
            return ResponseEntity.status(HttpStatus.OK).body("Employees added");
        } catch (DataIntegrityViolationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Employee already added");
        }
    }

    @GetMapping("/")
    public List<Employee> getAllEmployees(){
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{employeeId}")
    public Employee getEmployeeById(@PathVariable int employeeId){
        return employeeService.getAEmployeeById(employeeId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
