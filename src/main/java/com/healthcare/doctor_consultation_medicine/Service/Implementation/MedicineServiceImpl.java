package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Mapper.MedicineMapper;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicineServiceImpl implements MedicineService {
    private final MedicineRepository medicineRepository;

@Autowired
DoctorRepository doctorRepository;
@Autowired
    PatientRepository patientRepository;
    @Override
    public boolean isExistById(Long id) {
        return medicineRepository.existsById(id);
    }

    @Override
    public MedicineDto saveMedicine(MedicineDto medicineDto) {
        Medicine medicine = MedicineMapper.toMapEntity(medicineDto);
        return MedicineMapper.toMapDto(medicineRepository.save(medicine));
    }

    @Override
    public List<MedicineDto> getAllMedicine() {
        List<Medicine> medicineList = medicineRepository.findAll();
        return medicineList.stream().map(MedicineMapper::toMapDto).toList();
    }
    @Override
    public List<MedicineDto> getAllMedicineByPatientId(Long patientId){
        List<Medicine> medicineList = medicineRepository.findAllByPatientId(patientId);
        return medicineList.stream().map(MedicineMapper::toMapDto).collect(Collectors.toList());
    }

    @Override
    public MedicineDto getSingleMedicineById(Long id) {
        Optional<Medicine> retrievedMedicine = medicineRepository.findById(id);
        return MedicineMapper.toMapDto(retrievedMedicine.get());
    }

    @Override
    public void deleteMedicineById(Long id) {
    medicineRepository.deleteById(id);
    }

    @Override
    public void addMedicine(Long doctorId,Long patientId, MedicineDto medicine) {

        medicine.setDoctor(doctorRepository.findById(doctorId).get());
        medicine.setPatient(patientRepository.findById(patientId).get());
        medicine.setId(null);
        Medicine savedMedicine = medicineRepository.save(MedicineMapper.toMapEntity(medicine));
//        System.out.println("\n\n\nSavedMedicineNameInDatabase is :\t" + savedMedicine.getName());
    }

    @Override
    public void updateMedicine(Long medicineId, MedicineDto updatedMedicine) {
        Medicine medicine = medicineRepository.findById(medicineId).get();
        // Update medicine details
        medicine.setName(updatedMedicine.getName());
        medicine.setDosage(updatedMedicine.getDosage());
        medicine.setDuration(updatedMedicine.getDuration());
        medicineRepository.save(medicine);
    }
}
