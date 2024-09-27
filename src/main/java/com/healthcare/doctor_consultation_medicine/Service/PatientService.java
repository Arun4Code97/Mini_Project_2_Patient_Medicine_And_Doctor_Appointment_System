package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;

import java.util.List;
import java.util.Optional;


public interface PatientService {
    public PatientDto addPatient(PatientDto patientdto);
        public void updatePatientById(Long id,PatientDto patientDto) ;
        public void deletePatientById(Long id);
        public List<PatientDto> getAllPatient();
        public Optional<PatientDto> getPatientById(Long id);
        public PatientDto getSinglePatientById(Long id);
        public boolean existByEmailId(String email) ;

    Optional<PatientDto> findPatientByEmailId(String email);
}
