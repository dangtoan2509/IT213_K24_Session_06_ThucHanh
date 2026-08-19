package com.thuchanh.tool;

import com.thuchanh.model.Appointment;
import com.thuchanh.repository.ClinicRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppointmentManagementTools {

    private final ClinicRepository clinicRepository;

    public AppointmentManagementTools(ClinicRepository clinicRepository) {
        this.clinicRepository = clinicRepository;
    }

    @Tool(name = "getAppointmentDetails", description = "Tra cứu thông tin chi tiết lịch hẹn đã đặt theo mã đặt lịch")
    public String getAppointmentDetails(
            @ToolParam(description = "Mã đặt lịch hẹn (ví dụ: BOOK-101, BOOK-102)") String bookingCode) {
        if (bookingCode == null || bookingCode.isBlank()) {
            return "Vui lòng cung cấp mã đặt lịch hẹn hợp lệ.";
        }

        Optional<Appointment> appointmentOpt = clinicRepository.findByBookingCode(bookingCode);

        if (appointmentOpt.isEmpty()) {
            return "Không tìm thấy lịch hẹn với mã: " + bookingCode;
        }

        Appointment app = appointmentOpt.get();
        return String.format(
                "Thông tin lịch hẹn [%s]:\n" +
                "- Khách hàng: %s\n" +
                "- Số điện thoại: %s\n" +
                "- Bác sĩ: %s\n" +
                "- Dịch vụ: %s\n" +
                "- Thời gian khám: %s\n" +
                "- Trạng thái: %s",
                app.getBookingCode(),
                app.getCustomerName(),
                app.getCustomerPhone(),
                app.getDoctorName(),
                app.getServiceName(),
                app.getAppointmentDateTime(),
                app.getStatus()
        );
    }

    @Tool(name = "cancelOrReschedule", description = "Hủy hoặc chuyển đổi lịch hẹn sang thời gian khám mới")
    public String cancelOrReschedule(
            @ToolParam(description = "Mã đặt lịch hẹn (ví dụ: BOOK-101, BOOK-102)") String bookingCode,
            @ToolParam(description = "Hành động thực hiện: 'CANCEL' (hủy lịch) hoặc 'RESCHEDULE' (đổi lịch)") String action,
            @ToolParam(description = "Thời gian mới (định dạng YYYY-MM-DD HH:mm), bắt buộc nếu hành động là RESCHEDULE", required = false) String newDateTime) {

        if (bookingCode == null || bookingCode.isBlank()) {
            return "Vui lòng cung cấp mã đặt lịch hẹn hợp lệ.";
        }

        if (action == null || action.isBlank()) {
            return "Vui lòng chỉ định hành động là CANCEL (hủy) hoặc RESCHEDULE (đổi lịch).";
        }

        Optional<Appointment> appointmentOpt = clinicRepository.findByBookingCode(bookingCode);
        if (appointmentOpt.isEmpty()) {
            return "Thất bại: Không tìm thấy lịch hẹn với mã " + bookingCode;
        }

        Appointment app = appointmentOpt.get();
        String normalizedAction = action.trim().toUpperCase();

        if ("CANCEL".equals(normalizedAction) || "HỦY".equals(normalizedAction) || "HUY".equals(normalizedAction)) {
            boolean success = clinicRepository.cancelAppointment(bookingCode);
            if (success) {
                return String.format("Hủy lịch hẹn thành công cho mã [%s]. Lịch khám của khách hàng %s với %s vào ngày %s đã bị hủy.",
                        app.getBookingCode(), app.getCustomerName(), app.getDoctorName(), app.getAppointmentDateTime());
            } else {
                return "Không thể hủy lịch hẹn với mã: " + bookingCode;
            }
        } else if ("RESCHEDULE".equals(normalizedAction) || "ĐỔI".equals(normalizedAction) || "DOI".equals(normalizedAction)) {
            if (newDateTime == null || newDateTime.isBlank()) {
                return "Vui lòng cung cấp thời gian khám mới (newDateTime) để tiến hành đổi lịch.";
            }

            boolean success = clinicRepository.rescheduleAppointment(bookingCode, newDateTime);
            if (success) {
                return String.format("Đổi lịch hẹn thành công cho mã [%s]! Thời gian khám mới của khách hàng %s với %s là: %s.",
                        app.getBookingCode(), app.getCustomerName(), app.getDoctorName(), newDateTime);
            } else {
                return "Không thể đổi lịch hẹn với mã: " + bookingCode;
            }
        } else {
            return "Hành động không hợp lệ: '" + action + "'. Vui lòng chọn CANCEL (hủy) hoặc RESCHEDULE (đổi lịch).";
        }
    }
}
