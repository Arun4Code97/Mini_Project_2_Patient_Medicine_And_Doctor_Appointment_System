package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;

import java.util.List;

public interface MedicineService {
    public boolean isExistById(Long id);
    public MedicineDto saveMedicine(MedicineDto medicineDto);
    public List<MedicineDto> getAllMedicine();
    public MedicineDto getSingleMedicineById(Long id);
    public void deleteMedicineById(Long id);
//--------------------Including for doctor to add/update/delete medicines
public void addMedicine(Long doctorId, Long patientId,MedicineDto medicine) ;
public void updateMedicine(Long medicineId, MedicineDto updatedMedicine);
}
