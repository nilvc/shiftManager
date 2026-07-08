package com.shiftmanager.demo.Service;

import com.shiftmanager.demo.Entities.DTOs.ShiftChangeRequestDTO;
import com.shiftmanager.demo.Entities.DTOs.ShiftChangeRequestResolveDTO;
import com.shiftmanager.demo.Entities.DTOs.ShiftDTO;
import com.shiftmanager.demo.Entities.Employee;
import com.shiftmanager.demo.Entities.Shift;
import com.shiftmanager.demo.Entities.ShiftChangeRequest;
import com.shiftmanager.demo.Entities.ShiftChangeStatus;
import com.shiftmanager.demo.Repository.EmployeeRepository;
import com.shiftmanager.demo.Repository.ShiftChangeRequestRepository;
import com.shiftmanager.demo.Repository.ShiftRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShiftSwapService {

    private final ShiftChangeRequestRepository shiftChangeRequestRepository;
    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftSwapService(ShiftChangeRequestRepository shiftChangeRequestRepository,
                                EmployeeRepository employeeRepository,
                                ShiftRepository shiftRepository){
        this.shiftChangeRequestRepository = shiftChangeRequestRepository;
        this.employeeRepository = employeeRepository;
        this.shiftRepository = shiftRepository;
    }

    public ResponseEntity<String> createShitSwapRequest(ShiftChangeRequestDTO shiftChangeRequestDTO){
        int requestingEmployeeId = shiftChangeRequestDTO.getEmployeeId1();
        Employee requestingEmployee = employeeRepository.findById(requestingEmployeeId)
                .orElseThrow( () -> new RuntimeException("Requesting employeeId "+ requestingEmployeeId + " is invalid and not found"));
        int shift2EmployeeId = shiftChangeRequestDTO.getEmployeeId2();
        Employee shift2Employee = employeeRepository.findById(shift2EmployeeId)
                .orElseThrow( () -> new RuntimeException("Target employeeId "+ shift2EmployeeId + " is invalid and not found"));

        int currentShiftId = shiftChangeRequestDTO.getChangeShiftId1();
        Shift currentShift = shiftRepository.findById(currentShiftId)
                .orElseThrow( () -> new RuntimeException("Current shift id "+ currentShiftId + " is invalid/not found"));
        if (currentShift.getEmployeeId() != requestingEmployeeId){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requesting user is not owner of the shift");
        }
        int updateShiftId = shiftChangeRequestDTO.getChangeShiftId2();
        Shift updateShift = shiftRepository.findById(updateShiftId)
                .orElseThrow( () -> new RuntimeException("Update shift id "+ updateShiftId + " is invalid/not found"));
        if (updateShift.getEmployeeId() != shift2EmployeeId){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Target user is not owner of the target shift");
        }

        ShiftChangeRequest shiftChangeRequest = ShiftChangeRequest.builder()
                .changeShiftId1(currentShiftId)
                .changeShiftId2(updateShiftId)
                .employeeId1(requestingEmployeeId)
                .employeeId2(shift2EmployeeId)
                .build();
        shiftChangeRequestRepository.save(shiftChangeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Shift swap change request created");
    }


    public ResponseEntity<String> resolveShiftSwapRequest(ShiftChangeRequestResolveDTO shiftChangeRequestResolveDTO){
        int shiftChangeRequestId = shiftChangeRequestResolveDTO.getRequestID();
        ShiftChangeRequest shiftChangeRequest = shiftChangeRequestRepository.findById(shiftChangeRequestId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Shift swap request not found"));
        if(shiftChangeRequest.getStatus() != ShiftChangeStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Swap request is already resolved");
        }
        shiftChangeRequest.setStatus(shiftChangeRequestResolveDTO.getStatus());
        shiftChangeRequest.setComment(shiftChangeRequestResolveDTO.getComment());
        shiftChangeRequest.setUpdateTime(LocalDateTime.now());
        shiftChangeRequest.setUpdatedBy(shiftChangeRequestResolveDTO.getUpdatedBy());

        if(shiftChangeRequestResolveDTO.getStatus() == ShiftChangeStatus.APPROVED){
            // Update the shift owners
            int requesterShiftId = shiftChangeRequest.getChangeShiftId1();
            Shift requesterShift = shiftRepository.findById(requesterShiftId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid shift id "+requesterShiftId));

            int targetShiftId = shiftChangeRequest.getChangeShiftId2();
            Shift targetShift = shiftRepository.findById(targetShiftId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Invalid shift id "+requesterShiftId));

            int requesterEmployeeId = requesterShift.getEmployeeId();
            requesterShift.setEmployeeId(targetShift.getEmployeeId());
            targetShift.setEmployeeId(requesterEmployeeId);

            shiftRepository.save(requesterShift);
            shiftRepository.save(targetShift);

        }
        shiftChangeRequestRepository.save(shiftChangeRequest);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Shift change request approved");
    }


    public List<ShiftChangeRequest> getAllShiftForEmployee(int employeeId){
        return shiftChangeRequestRepository.findByEmployeeId1OrEmployeeId2(employeeId,employeeId);
    }
}
