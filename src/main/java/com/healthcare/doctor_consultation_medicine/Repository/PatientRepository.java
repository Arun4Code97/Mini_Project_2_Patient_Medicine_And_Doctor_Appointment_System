package com.healthcare.doctor_consultation_medicine.Repository;

import com.healthcare.doctor_consultation_medicine.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long> {
    Optional<Patient> findByEmail(String emailId);
}
