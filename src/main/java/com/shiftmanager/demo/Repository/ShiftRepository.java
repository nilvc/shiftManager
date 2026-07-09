package com.shiftmanager.demo.Repository;

import com.shiftmanager.demo.Entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift,Integer> {
    List<Shift> findByEmployeeId(int employeeId);
}
