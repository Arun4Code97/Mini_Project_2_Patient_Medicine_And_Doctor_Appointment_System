package com.healthcare.doctor_consultation_medicine.Mapper;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;

public class DoctorMapper {
    public static Doctor mapToEntity(DoctorDto doctorDto){
        Long id;
        if (doctorDto.getId() != null && !doctorDto.getId().isEmpty()) {
            try {
                id= Long.parseLong(doctorDto.getId());
            } catch (NumberFormatException e) {
                // Handle invalid id format
                id=null;
            }
        } else {
            // Set to null for new records
            id=null;
        }

        return new Doctor(
                id,
                doctorDto.getFirstName(),
                doctorDto.getLastName(),
                doctorDto.getPassword(),
                doctorDto.getGender(),
                doctorDto.getEmail(),
                doctorDto.getSpecialization(),
                doctorDto.getQualification(),
                Integer.parseInt(doctorDto.getExperience()),
                Long.parseLong(doctorDto.getPhone()),
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
                doctor.getPassword(),
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
