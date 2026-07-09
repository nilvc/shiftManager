package com.shiftmanager.demo.Repository;

import com.shiftmanager.demo.Entities.ShiftChangeRequest;
import com.shiftmanager.demo.Entities.ShiftChangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ShiftChangeRequestRepository extends JpaRepository<ShiftChangeRequest, Integer> {
    List<ShiftChangeRequest> findByEmployeeId1OrEmployeeId2(int employeeId1, int employeeId2);
    List<ShiftChangeRequest> findByStatus(ShiftChangeStatus status);
}
