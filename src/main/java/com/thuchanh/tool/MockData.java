package com.thuchanh.tool;

import com.thuchanh.dto.DoctorDto;
import com.thuchanh.dto.ServiceDto;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static final List<DoctorDto> DOCTORS = new ArrayList<>();
    public static final List<ServiceDto> SERVICES = new ArrayList<>();

    static {
        // Mock Doctors
        DOCTORS.add(new DoctorDto("Bác sĩ Nam", "Tiểu phẫu", 10));
        DOCTORS.add(new DoctorDto("Bác sĩ Lan", "Nha chu", 8));
        DOCTORS.add(new DoctorDto("Bác sĩ Hùng", "Chỉnh nha", 15));
        DOCTORS.add(new DoctorDto("Bác sĩ Mai", "Nhổ răng", 5));
        
        // Mock Services
        SERVICES.add(new ServiceDto("Nhổ răng khôn", 1500000, "Nhổ răng khôn mọc ngầm, mọc lệch, an toàn không đau."));
        SERVICES.add(new ServiceDto("Niềng răng mắc cài", 30000000, "Niềng răng chỉnh nha bằng mắc cài kim loại cao cấp."));
        SERVICES.add(new ServiceDto("Bọc răng sứ", 5000000, "Bọc răng sứ thẩm mỹ, độ bền cao, màu sắc tự nhiên."));
        SERVICES.add(new ServiceDto("Lấy cao răng", 300000, "Vệ sinh răng miệng, lấy cao răng siêu âm nhẹ nhàng."));
        SERVICES.add(new ServiceDto("Tẩy trắng răng", 2500000, "Tẩy trắng răng công nghệ laser nhanh chóng, hiệu quả."));
    }
}
