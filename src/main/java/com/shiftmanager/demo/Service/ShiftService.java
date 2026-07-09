package com.shiftmanager.demo.Service;

import com.shiftmanager.demo.Entities.Shift;
import com.shiftmanager.demo.Repository.ShiftRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository){
        this.shiftRepository = shiftRepository;
    }

    public void addShifts(){

        // Adding dummy shifts

        Shift shift1 = Shift.builder()
                .employeeId(3)
                .startTime(LocalDateTime.of(2026, 1, 1, 13, 0, 0))
                .endTime(LocalDateTime.of(2026, 1, 1, 15, 0, 0))
                .build();

        shiftRepository.save(shift1);

        Shift shift2 = Shift.builder()
                .employeeId(4)
                .startTime(LocalDateTime.of(2026, 1, 2, 13, 0, 0))
                .endTime(LocalDateTime.of(2026, 1, 2, 15, 0, 0))
                .build();

        shiftRepository.save(shift2);

        Shift shift3 = Shift.builder()
                .employeeId(5)
                .startTime(LocalDateTime.of(2026, 1, 3, 13, 0, 0))
                .endTime(LocalDateTime.of(2026, 1, 3, 15, 0, 0))
                .build();

        shiftRepository.save(shift3);
    }


    public List<Shift> getAllShifts(){
        return shiftRepository.findAll();
    }

    public Shift getShiftById(int shiftId){
        return shiftRepository.findById(shiftId).orElseThrow(() -> new RuntimeException("Invalid shift id " + shiftId));
    }

    public List<Shift> getShiftsForEmployee(int employeeId){
        return shiftRepository.findByEmployeeId(employeeId);
    }

}

