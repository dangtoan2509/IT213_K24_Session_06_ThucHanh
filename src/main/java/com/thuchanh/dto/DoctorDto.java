package com.thuchanh.dto;

public class DoctorDto {
    private String name;
    private String specialty;
    private int experienceYears;

    public DoctorDto(String name, String specialty, int experienceYears) {
        this.name = name;
        this.specialty = specialty;
        this.experienceYears = experienceYears;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }
}
