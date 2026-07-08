package com.shiftmanager.demo.Entities.DTOs;

import lombok.Data;

import java.time.ZonedDateTime;


public record ShiftDTO(ZonedDateTime startTime, ZonedDateTime endTime, String status, int employeeId) {
}
