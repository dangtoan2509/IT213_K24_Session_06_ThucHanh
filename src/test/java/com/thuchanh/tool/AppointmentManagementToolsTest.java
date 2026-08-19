package com.thuchanh.tool;

import com.thuchanh.repository.ClinicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentManagementToolsTest {

    private ClinicRepository clinicRepository;
    private AppointmentManagementTools appointmentManagementTools;

    @BeforeEach
    void setUp() {
        clinicRepository = new ClinicRepository();
        appointmentManagementTools = new AppointmentManagementTools(clinicRepository);
    }

    @Test
    void testGetAppointmentDetails_Success() {
        String result = appointmentManagementTools.getAppointmentDetails("BOOK-102");
        assertNotNull(result);
        assertTrue(result.contains("BOOK-102"));
        assertTrue(result.contains("Hoàng"));
        assertTrue(result.contains("Bác sĩ Nam"));
    }

    @Test
    void testGetAppointmentDetails_NotFound() {
        String result = appointmentManagementTools.getAppointmentDetails("BOOK-999");
        assertNotNull(result);
        assertTrue(result.contains("Không tìm thấy"));
    }

    @Test
    void testCancelAppointment_Success() {
        String result = appointmentManagementTools.cancelOrReschedule("BOOK-101", "CANCEL", null);
        assertNotNull(result);
        assertTrue(result.contains("Hủy lịch hẹn thành công"));

        String details = appointmentManagementTools.getAppointmentDetails("BOOK-101");
        assertTrue(details.contains("CANCELLED"));
    }

    @Test
    void testRescheduleAppointment_Success() {
        String result = appointmentManagementTools.cancelOrReschedule("BOOK-102", "RESCHEDULE", "2026-08-21 15:00");
        assertNotNull(result);
        assertTrue(result.contains("Đổi lịch hẹn thành công"));

        String details = appointmentManagementTools.getAppointmentDetails("BOOK-102");
        assertTrue(details.contains("2026-08-21 15:00"));
        assertTrue(details.contains("RESCHEDULED"));
    }

    @Test
    void testCancelOrReschedule_InvalidAction() {
        String result = appointmentManagementTools.cancelOrReschedule("BOOK-101", "UNKNOWN", null);
        assertNotNull(result);
        assertTrue(result.contains("Hành động không hợp lệ"));
    }
}
