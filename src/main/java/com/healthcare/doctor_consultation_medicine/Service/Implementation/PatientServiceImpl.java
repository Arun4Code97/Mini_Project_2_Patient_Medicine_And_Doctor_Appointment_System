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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
private final MedicineRepository medicineRepository;
    public PatientDto addPatient(PatientDto patientdto) {

            Patient newPatient = PatientMapper.toMapPatientEntity(patientdto);
            Patient savedPatient = patientRepository.save(newPatient);
            return PatientMapper.toMapPatientDto(savedPatient);
    }


    public void updatePatientById(Long id,PatientDto patientDto) {
        boolean isIdExist = patientRepository.existsById(id);
        if(isIdExist){
        Patient updatePatient = PatientMapper.toMapPatientEntity(patientDto);
        Patient savedPatient = patientRepository.save(updatePatient);
        }
    }


@Transactional
public void deletePatientById(Long id){
        if(patientRepository.existsById(id)){
            appointmentRepository.deleteByPatientId(id);
            medicineRepository.deleteByPatientId(id);
            patientRepository.deleteById(id);}
}

//    public boolean deletePatientById(Long id) {
//        if (patientRepository.existsById(id)) {
//            patientRepository.deleteById(id);
//            return true;
//        } else
//           return false;
//    }

    public List<PatientDto> getAllPatient() {
        List<Patient> retrivedAllPatients = patientRepository.findAll();

        if(!retrivedAllPatients.isEmpty())
            return retrivedAllPatients.stream().map(PatientMapper::toMapPatientDto).toList();
      return null;
    }

    public Optional<PatientDto> getPatientById(Long id) {
        Optional<Patient> retrievedPatient = patientRepository.findById(id);
        return retrievedPatient.map(PatientMapper::toMapPatientDto);
    }
    public PatientDto getSinglePatientById(Long id){
        return patientRepository.findById(id).map(PatientMapper::toMapPatientDto).orElse(new PatientDto());
    }
    public boolean existByEmailId(String email) {
        Optional<Patient> patient = patientRepository.findOneByEmail(email);
        return patient.isPresent();
    }
    public Optional<PatientDto> findPatientByEmailId(String email){
        Optional<Patient> retrievedPatient = patientRepository.findOneByEmail(email);
        return retrievedPatient.map(PatientMapper::toMapPatientDto);
    }

}
