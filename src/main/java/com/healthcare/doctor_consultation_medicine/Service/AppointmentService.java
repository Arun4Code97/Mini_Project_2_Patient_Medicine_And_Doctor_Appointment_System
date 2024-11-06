package com.healthcare.doctor_consultation_medicine.Service;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    List<Appointment> collectBookedSlots(Long doctorId, LocalDate date);
    Appointment bookAppointment(Long doctorId, Long patientId, LocalDate appointmentDate, LocalTime appointmentTime);

    PatientDto fetchPatientRecord(Long doctorId, LocalDate date, LocalTime time);

    Appointment findById(Long appointmentId);

    Appointment addAdviseToAppointment(Appointment appointment); // for saving doctor advise

    Appointment fetchAppointment(Long doctorId, LocalDate date, LocalTime time);
}
