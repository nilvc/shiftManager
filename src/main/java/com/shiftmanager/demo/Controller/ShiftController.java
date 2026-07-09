package com.shiftmanager.demo.Controller;

import com.shiftmanager.demo.Entities.Shift;
import com.shiftmanager.demo.Service.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("shift")
public class ShiftController {

    ShiftService shiftService;

    private static final Logger log = LoggerFactory.getLogger(ShiftController.class);

    public ShiftController(ShiftService shiftService){
        this.shiftService = shiftService;
    }

    @GetMapping("/{shiftId}")
    public Shift getShiftById( @PathVariable int shiftId){
        log.info("Checking shift for shiftId - {}", shiftId);
        return shiftService.getShiftById(shiftId);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Shift> getShiftsForEmployee(@PathVariable int employeeId){
        log.info("Checking shifts for employeeId - {}", employeeId);
        return shiftService.getShiftsForEmployee(employeeId);
    }

    @PostMapping("/addDummyShifts")
    public ResponseEntity<String> addDummyShifts(){
        shiftService.addShifts();
        return ResponseEntity.status(200).body("Shifts added");
    }

}
