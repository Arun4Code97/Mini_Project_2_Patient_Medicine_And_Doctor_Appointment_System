package com.healthcare.doctor_consultation_medicine.Repository;

import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicineRepository extends JpaRepository<Medicine,Long> {
}
