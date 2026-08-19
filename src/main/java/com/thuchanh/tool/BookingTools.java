package com.thuchanh.tool;

import com.thuchanh.dto.BookingResultDto;
import com.thuchanh.model.Appointment;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class BookingTools {

    // Danh sách lưu trữ lịch hẹn tạm thời (in-memory)
    private final List<Appointment> appointments = new ArrayList<>();

    // Các ca khám mặc định trong ngày
    private final List<String> defaultSlots = List.of(
            "09:00", "10:00", "11:00", "14:00", "15:00", "16:00"
    );

    @Tool(description = "Kiểm tra các khung giờ trống của bác sĩ trong một ngày cụ thể")
    public List<String> checkAvailableSlots(
            @ToolParam(description = "Tên bác sĩ") String doctorName, 
            @ToolParam(description = "Ngày cần kiểm tra theo định dạng YYYY-MM-DD") String date) {
        
        // Lọc các cuộc hẹn của bác sĩ trong ngày cụ thể
        List<String> bookedSlots = appointments.stream()
                .filter(app -> app.getDoctorName().equalsIgnoreCase(doctorName) 
                        && app.getAppointmentDateTime().startsWith(date))
                .map(app -> app.getAppointmentDateTime().split(" ")[1])
                .collect(Collectors.toList());

        // Tìm các giờ trống
        return defaultSlots.stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    @Tool(description = "Đặt lịch khám bệnh sau khi đã có đầy đủ thông tin khách hàng")
    public BookingResultDto bookAppointment(
            @ToolParam(description = "Họ và tên khách hàng") String customerName, 
            @ToolParam(description = "Số điện thoại khách hàng") String customerPhone, 
            @ToolParam(description = "Tên bác sĩ") String doctorName, 
            @ToolParam(description = "Tên dịch vụ nha khoa") String serviceName, 
            @ToolParam(description = "Ngày giờ khám theo định dạng YYYY-MM-DD HH:mm") String appointmentDateTime) {
        
        // Kiểm tra xem lịch này đã có ai đặt chưa (check trùng lặp)
        boolean isBooked = appointments.stream()
                .anyMatch(app -> app.getDoctorName().equalsIgnoreCase(doctorName)
                        && app.getAppointmentDateTime().equals(appointmentDateTime));

        if (isBooked) {
            return BookingResultDto.builder()
                    .status("FAILED")
                    .message("Bác sĩ " + doctorName + " đã có lịch bận vào lúc " + appointmentDateTime + ". Vui lòng chọn giờ khác.")
                    .build();
        }

        // Tạo lịch hẹn mới
        String bookingCode = "BOOK-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        
        Appointment newAppointment = Appointment.builder()
                .bookingCode(bookingCode)
                .customerName(customerName)
                .customerPhone(customerPhone)
                .doctorName(doctorName)
                .serviceName(serviceName)
                .appointmentDateTime(appointmentDateTime)
                .status("CONFIRMED")
                .build();
                
        appointments.add(newAppointment);

        return BookingResultDto.builder()
                .bookingCode(bookingCode)
                .status("SUCCESS")
                .message("Đặt lịch thành công cho dịch vụ " + serviceName + " với " + doctorName + " vào " + appointmentDateTime)
                .build();
    }
}
