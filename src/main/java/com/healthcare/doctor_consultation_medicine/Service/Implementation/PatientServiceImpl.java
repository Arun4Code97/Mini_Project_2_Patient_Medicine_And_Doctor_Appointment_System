package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Mapper.PatientMapper;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicineRepository medicineRepository;
    @Override
    public boolean existByEmailId(String email) {
        Optional<Patient> patient = patientRepository.findOneByEmail(email);
        return patient.isPresent();
    }
    @Override
    public PatientDto addPatient(PatientDto patientdto) {
            Patient newPatient = PatientMapper.toMapPatientEntity(patientdto);
            Patient savedPatient = patientRepository.save(newPatient);
            return PatientMapper.toMapPatientDto(savedPatient);
    }
    @Override
    public void setPassword(Long patientId, String confirmPassword) {
        patientRepository.findById(patientId).ifPresent(
                patient -> {
                    patient.setPassword(confirmPassword);
                    patientRepository.save(patient);
                } );
    }
    @Override
    public PatientDto getSinglePatientById(Long id){
        return patientRepository.findById(id).map(PatientMapper::toMapPatientDto)
                .orElse(new PatientDto());
    }
    @Override
    public Optional<PatientDto> findPatientByEmailId(String email){
        Optional<Patient> retrievedPatient = patientRepository.findOneByEmail(email);
        return retrievedPatient.map(PatientMapper::toMapPatientDto);
    }
    @Override
    public PatientDto updatePatientById(Long id, PatientDto patientDto) {
        boolean isIdExist = patientRepository.existsById(id);
        if(isIdExist){
        Patient updatePatient = PatientMapper.toMapPatientEntity(patientDto);
        Patient savedPatient = patientRepository.save(updatePatient);
        return PatientMapper.toMapPatientDto(savedPatient);
        }
        else return null;
    }
    @Transactional
    @Override
    public void deletePatientById(Long id){
        if(patientRepository.existsById(id)){
            appointmentRepository.deleteByPatientId(id);
            medicineRepository.deleteByPatientId(id);
            patientRepository.deleteById(id);
        }
    }
}
