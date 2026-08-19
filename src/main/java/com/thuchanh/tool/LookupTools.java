package com.thuchanh.tool;

import com.thuchanh.dto.DoctorDto;
import com.thuchanh.dto.ServiceDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class LookupTools {

    @Tool(description = "Tra cứu thông tin và bảng giá dịch vụ nha khoa theo tên dịch vụ (từ khóa).")
    public List<ServiceDto> searchServices(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return MockData.SERVICES;
        }
        String lowerKeyword = keyword.toLowerCase();
        return MockData.SERVICES.stream()
                .filter(s -> s.getName().toLowerCase().contains(lowerKeyword) || 
                             s.getDescription().toLowerCase().contains(lowerKeyword))
                .collect(Collectors.toList());
    }

    @Tool(description = "Tìm kiếm bác sĩ theo chuyên khoa hoặc xem toàn bộ danh sách bác sĩ.")
    public List<DoctorDto> getDoctorsBySpecialty(String specialty) {
        if (specialty == null || specialty.trim().isEmpty()) {
            return MockData.DOCTORS;
        }
        String lowerSpecialty = specialty.toLowerCase();
        return MockData.DOCTORS.stream()
                .filter(d -> d.getSpecialty().toLowerCase().contains(lowerSpecialty))
                .collect(Collectors.toList());
    }
}
