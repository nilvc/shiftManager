package com.shiftmanager.demo.Controller;

import com.shiftmanager.demo.Entities.DTOs.ShiftChangeRequestDTO;
import com.shiftmanager.demo.Entities.DTOs.ShiftChangeRequestResolveDTO;
import com.shiftmanager.demo.Entities.ShiftChangeRequest;
import com.shiftmanager.demo.Service.ShiftSwapService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.PublicKey;
import java.util.List;


@RestController
@RequestMapping("shiftsSwap")
public class ShiftSwapController {

    private final ShiftSwapService shiftSwapService;

    public ShiftSwapController(ShiftSwapService shiftSwapService){
        this.shiftSwapService = shiftSwapService;
    }

    @PostMapping("/swap")
    public ResponseEntity<String> swapShifts(@RequestBody ShiftChangeRequestDTO shiftChangeRequestDTO){
        return shiftSwapService.createShitSwapRequest(shiftChangeRequestDTO);
    }

    @PostMapping("/resolve")
    public ResponseEntity<String> resolveShiftSwaps( @RequestBody ShiftChangeRequestResolveDTO shiftChangeRequestResolveDTO){
        return shiftSwapService.resolveShiftSwapRequest(shiftChangeRequestResolveDTO);
    }

    public ResponseEntity<List<ShiftChangeRequest>> getAllPendingSwapRequest(int employeeId){
        return ResponseEntity.ok(shiftSwapService.getAllShiftForEmployee(employeeId));
    }

 }
