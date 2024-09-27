package com.healthcare.doctor_consultation_medicine.Repository;

import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
//    List<Appointment> findByDoctorAndAppointmentDate(Long doctorId, LocalDate appointmentDate);

// Native query using doctor_id and appointment_date
@Query(value = "SELECT * FROM appointment WHERE doctor_id = :doctorId AND appointment_date = :appointmentDate", nativeQuery = true)
List<Appointment> findByDoctorIdAndAppointmentDate(@Param("doctorId") Long doctorId, @Param("appointmentDate") LocalDate appointmentDate);
    List<Appointment> findAllByPatient(Patient patient);

    Appointment findByDoctorIdAndAppointmentDateAndAppointmentTime(Long doctorId, LocalDate date, LocalTime time);

    void deleteByPatientId(Long patientId);
}
