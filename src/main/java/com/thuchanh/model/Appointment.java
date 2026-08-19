package com.thuchanh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private String bookingCode;
    private String customerName;
    private String customerPhone;
    private String doctorName;
    private String serviceName;
    private String appointmentDateTime;
    private String status;
}

