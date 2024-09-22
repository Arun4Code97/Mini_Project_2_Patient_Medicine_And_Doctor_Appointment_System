package com.healthcare.doctor_consultation_medicine.Mapper;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;

public class MedicineMapper {
    public static Medicine toMapEntity(MedicineDto dto){
        Long id;
        // Convert String id to Long (if valid)
        if (dto.getId() != null && !dto.getId().isEmpty()) {
            try {
                id= Long.parseLong(dto.getId());
            } catch (NumberFormatException e) {
                // Handle invalid id format
                id=null;
            }
        } else {
            // Set to null for new records
            id=null;
        }

        return new Medicine(
                id,
                dto.getName(),
                dto.getDosage(),
                dto.getDuration(),
                dto.getDoctor(),
                dto.getPatient()
        );
    }

    public static MedicineDto toMapDto(Medicine entity){
        return new MedicineDto(entity.getId().toString(),
                entity.getName(),
                entity.getDosage(),
                entity.getDuration(),
                entity.getDoctor(),
                entity.getPatient()
        );
    }

}
