package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DoctorService {
    public DoctorDto saveDoctorWithImage(DoctorDto doctorDto, MultipartFile imageFile) throws IOException;
    boolean isExistByEmail(String email);

    boolean isExistById(Long id);

    DoctorDto getSingleDoctor(Long id);
    List<DoctorDto> getAllDoctors();

//    DoctorDto updateDoctorById(Long id, DoctorDto doctorDto);

    void deletePatientById(Long id);
}
