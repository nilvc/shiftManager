package com.shiftmanager.demo.Entities.DTOs;


import com.shiftmanager.demo.Entities.ShiftChangeStatus;
import lombok.Getter;


@Getter
public class ShiftChangeRequestResolveDTO {
    int requestID;
    ShiftChangeStatus status;
    int updatedBy;
    String comment;

}
