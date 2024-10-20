package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    public DoctorDto saveDoctorWithImage(DoctorDto doctorDto);
    public void setPassword(Long doctorId, String confirmPassword) ;
    public boolean isExistByEmail(String email) ;
    public boolean isExistById(Long id) ;
    public DoctorDto getSingleDoctorById(Long id) ;
    public Optional<DoctorDto> findDoctorByEmailId(String email);
    public List<DoctorDto> getAllDoctors() ;
    public void deleteDoctorById(Long id);
    }
