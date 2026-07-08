package com.shiftmanager.demo.Repository;

import com.shiftmanager.demo.Entities.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift,Integer> {
}
