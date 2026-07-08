package com.shiftmanager.demo.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@Table(name = "Shifts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Shift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer shiftId; // <-- Changed from int to Integer (Defaults to null)

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Builder.Default
    private ShiftStatus status = ShiftStatus.PENDING;
    private int employeeId; // Primitive int is fine here if employeeId can never be null
}

