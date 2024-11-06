package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Mapper.MedicineMapper;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    @Override
    public MedicineDto addMedicine(Long doctorId, Long patientId, MedicineDto medicineDto) {

        medicineDto.setDoctor(doctorRepository.findById(doctorId)
                .orElseThrow(() -> new NoSuchElementException("Doctor id " + doctorId +" not found")));
        medicineDto.setPatient(patientRepository.findById(patientId)
                .orElseThrow(() -> new NoSuchElementException("Patient id " + patientId + " not found")));
        medicineDto.setId(null);
        Medicine savedMedicine = medicineRepository.save(MedicineMapper.toMapEntity(medicineDto));
        return MedicineMapper.toMapDto(savedMedicine);
    }
    @Override
    public MedicineDto getSingleMedicineById(Long id) {
        Optional<Medicine> retrievedMedicine = medicineRepository.findById(id);
        return retrievedMedicine.map(MedicineMapper::toMapDto).orElse(null);
    }
    @Override
    public List<MedicineDto> getAllMedicineByPatientId(Long patientId){
        List<Medicine> medicineList = medicineRepository.findAllByPatientId(patientId);
        return medicineList.stream().map(MedicineMapper::toMapDto).collect(Collectors.toList());
    }
    @Override
    public MedicineDto updateMedicine(Long medicineId, MedicineDto updatedMedicine) {
        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new NoSuchElementException("Medicine with id " + medicineId + " not found"));
        // Update medicine details
        medicine.setId(medicineId);
        medicine.setName(updatedMedicine.getName());
        medicine.setDosage(updatedMedicine.getDosage());
        medicine.setDuration(updatedMedicine.getDuration());
        return MedicineMapper.toMapDto(medicineRepository.save(medicine));
    }
    @Override
    public void deleteMedicineById(Long id) {
        if(medicineRepository.existsById(id))
            medicineRepository.deleteById(id);
    }
}
