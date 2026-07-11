package com.shiftmanager.demo.Service;

import com.shiftmanager.demo.Entities.Employee;
import com.shiftmanager.demo.Repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository){
        this.employeeRepository = employeeRepository;
    }

    public void createAndAddEmployees(){

//        creating employees

        Employee employee1 = new Employee(0,"Gaurav","Gaurav@gmail.com", -1);
        employeeRepository.save(employee1);

        Employee employee2 = new Employee(0,"Vinayak","Vinayak@gmail.com", -1);
        employeeRepository.save(employee2);

        Employee employee3 = new Employee(0,"Swapnil","Swapnil@gmail.com", 2);
        employeeRepository.save(employee3);

        Employee employee4 = new Employee(0,"Raghav","Raghav@gmail.com", 1);
        employeeRepository.save(employee4);

        Employee employee5 = new Employee(0,"Mehul","Mehul@gmail.com", 1);
        employeeRepository.save(employee5);

        Employee employee6 = new Employee(0,"Nilesh","Nilesh@gmail.com", 2);
        employeeRepository.save(employee6);

    }

    public List<Employee> getAllEmployees(){
        return employeeRepository.findAll();
    }

    public Optional<Employee> getAEmployeeById(int employeeId){
        return employeeRepository.findById(employeeId);
    }
}
