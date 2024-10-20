package com.healthcare.doctor_consultation_medicine.Repository;

import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    Optional<Doctor> findOneByEmail(String email);

}

