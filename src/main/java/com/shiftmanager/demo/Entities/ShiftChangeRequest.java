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
    @Column(nullable = false)
    int changeShiftId1;
    @Column(nullable = false)
    int changeShiftId2;
    @Column(nullable = false)
    int employeeId1;
    @Column(nullable = false)
    int employeeId2;
    String comment;
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();
}
