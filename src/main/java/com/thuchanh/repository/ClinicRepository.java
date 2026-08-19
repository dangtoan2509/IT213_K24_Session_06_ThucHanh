package com.thuchanh.repository;

import com.thuchanh.model.Appointment;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ClinicRepository {

    private final Map<String, Appointment> appointments = new ConcurrentHashMap<>();

    public ClinicRepository() {
        Appointment app1 = Appointment.builder()
                .bookingCode("BOOK-101")
                .customerName("Nguyễn Văn A")
                .customerPhone("0901234567")
                .doctorName("Bác sĩ Nam")
                .serviceName("Niềng răng")
                .appointmentDateTime("2026-08-20 14:00")
                .status("CONFIRMED")
                .build();

        Appointment app2 = Appointment.builder()
                .bookingCode("BOOK-102")
                .customerName("Hoàng")
                .customerPhone("0912345678")
                .doctorName("Bác sĩ Nam")
                .serviceName("Nhổ răng khôn")
                .appointmentDateTime("2026-08-21 09:00")
                .status("CONFIRMED")
                .build();

        appointments.put(app1.getBookingCode(), app1);
        appointments.put(app2.getBookingCode(), app2);
    }

    public Optional<Appointment> findByBookingCode(String bookingCode) {
        if (bookingCode == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(appointments.get(bookingCode.trim().toUpperCase()));
    }

    public void save(Appointment appointment) {
        if (appointment != null && appointment.getBookingCode() != null) {
            appointments.put(appointment.getBookingCode().trim().toUpperCase(), appointment);
        }
    }

    public List<Appointment> findAll() {
        return new ArrayList<>(appointments.values());
    }

    public boolean cancelAppointment(String bookingCode) {
        Optional<Appointment> opt = findByBookingCode(bookingCode);
        if (opt.isPresent()) {
            Appointment app = opt.get();
            app.setStatus("CANCELLED");
            return true;
        }
        return false;
    }

    public boolean rescheduleAppointment(String bookingCode, String newDateTime) {
        Optional<Appointment> opt = findByBookingCode(bookingCode);
        if (opt.isPresent()) {
            Appointment app = opt.get();
            app.setAppointmentDateTime(newDateTime);
            app.setStatus("RESCHEDULED");
            return true;
        }
        return false;
    }
}

