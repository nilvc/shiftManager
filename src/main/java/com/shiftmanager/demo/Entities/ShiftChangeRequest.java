package com.shiftmanager.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShiftChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer requestId;
    @Column(nullable = false)
    @Builder.Default
    ShiftChangeStatus status = ShiftChangeStatus.PENDING;
    Integer updatedBy;
    LocalDateTime updateTime;
    int changeShiftId1;
    int changeShiftId2;
    int employeeId1;
    int employeeId2;
    String comment;
    @Column(nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
}
