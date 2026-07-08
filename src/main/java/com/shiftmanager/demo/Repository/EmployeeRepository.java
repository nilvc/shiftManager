package com.shiftmanager.demo.Repository;

import com.shiftmanager.demo.Entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee,Integer> {
}
