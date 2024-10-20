package com.healthcare.doctor_consultation_medicine.Repository;

import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicineRepository extends JpaRepository<Medicine,Long> {
    List<Medicine> findAllByPatientId(Long patientId);
    void deleteByPatientId(Long id);
    void deleteByDoctorId(Long id);
}
