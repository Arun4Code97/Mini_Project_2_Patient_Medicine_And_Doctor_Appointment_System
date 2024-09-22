package com.healthcare.doctor_consultation_medicine.Mapper;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;

public class DoctorMapper {
    public static Doctor mapToEntity(DoctorDto doctorDto){
        return new Doctor(
                Long.parseLong(doctorDto.getId()),
                doctorDto.getFirstName(),
                doctorDto.getLastName(),
                doctorDto.getGender(),
                doctorDto.getEmail(),
                doctorDto.getSpecialization(),
                doctorDto.getQualification(),
                Integer.parseInt(doctorDto.getExperience()),
                Long.parseLong(doctorDto.getPhoneNumber()),
                doctorDto.getImage(),
                doctorDto.getMedicines().stream().map(MedicineMapper::toMapEntity).toList(),
                doctorDto.getAppointments().stream().toList()
                );
    }

    public static DoctorDto mapToDto(Doctor doctor){
        return new DoctorDto(
                doctor.getId().toString(),
                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getGender(),
                doctor.getEmail(),
                doctor.getSpecialization(),
                doctor.getQualification(),
                doctor.getExperience().toString(),
                doctor.getPhoneNumber().toString(),
                doctor.getImage(),
                doctor.getMedicines().stream().map(MedicineMapper::toMapDto).toList(),
                doctor.getAppointments().stream().toList()
                );
    }
}
