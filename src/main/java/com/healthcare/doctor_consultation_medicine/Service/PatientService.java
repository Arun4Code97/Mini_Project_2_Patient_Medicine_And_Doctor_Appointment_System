package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;

import java.util.List;
import java.util.Optional;


public interface PatientService {
    public boolean existByEmailId(String email) ;
    public PatientDto addPatient(PatientDto patientdto) ;
    public void setPassword(Long patientId, String confirmPassword);
    public PatientDto getSinglePatientById(Long id);
    public Optional<PatientDto> findPatientByEmailId(String email);
    public void updatePatientById(Long id,PatientDto patientDto);
    public void deletePatientById(Long id);
    }
