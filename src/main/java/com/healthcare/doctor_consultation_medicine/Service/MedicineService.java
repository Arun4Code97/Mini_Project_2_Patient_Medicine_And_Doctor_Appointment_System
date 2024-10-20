package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;

import java.util.List;

public interface MedicineService {
    public MedicineDto addMedicine(Long doctorId, Long patientId, MedicineDto medicine) ;
    public List<MedicineDto> getAllMedicineByPatientId(Long patientId);
    public MedicineDto getSingleMedicineById(Long id);
    public void updateMedicine(Long medicineId, MedicineDto updatedMedicine);
    public void deleteMedicineById(Long id);
}
