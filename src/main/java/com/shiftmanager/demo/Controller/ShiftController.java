package com.shiftmanager.demo.Controller;

import com.shiftmanager.demo.Entities.DTOs.ShiftDTO;
import com.shiftmanager.demo.Entities.Shift;
import com.shiftmanager.demo.Repository.ShiftRepository;
import com.shiftmanager.demo.Service.ShiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("shift")
public class ShiftController {

    ShiftService shiftService;

    private static final Logger log = LoggerFactory.getLogger(ShiftController.class);

    public ShiftController(ShiftService shiftService){
        this.shiftService = shiftService;
    }

    @GetMapping("/")
    public ResponseEntity<String> great(){
        return ResponseEntity.status(200).body("Hello");
    }

    @GetMapping("/{shiftId}")
    public Shift getShiftById( @PathVariable int shiftId){
        log.info("Checking shift for shiftId - {}", shiftId);
        return shiftService.getShiftById(shiftId);
    }

    @PostMapping("/addDummyShifts")
    public ResponseEntity<String> addDummyShifts(){
        shiftService.addShifts();
        return ResponseEntity.status(200).body("Shifts added");
    }

}
