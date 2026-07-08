package com.shiftmanager.demo.Entities.DTOs;

import lombok.Data;


@Data
public class ShiftChangeRequestDTO {
    Integer changeShiftId1;
    Integer changeShiftId2;
    Integer employeeId1;
    Integer employeeId2;
}
