package com.shiftmanager.demo.Entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Employees")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int employeeID;
    @Column(nullable = false)
    String name;
    @Column(name = "email", unique = true, nullable = false)
    String email;
    int managerId;
}
