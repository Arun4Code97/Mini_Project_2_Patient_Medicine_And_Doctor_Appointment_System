package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;

import java.util.List;

public interface MedicineService {
    MedicineDto addMedicine(Long doctorId, Long patientId, MedicineDto medicine) ;
    List<MedicineDto> getAllMedicineByPatientId(Long patientId);
    MedicineDto getSingleMedicineById(Long id);
    MedicineDto updateMedicine(Long medicineId, MedicineDto updatedMedicine);
    void deleteMedicineById(Long id);
}
