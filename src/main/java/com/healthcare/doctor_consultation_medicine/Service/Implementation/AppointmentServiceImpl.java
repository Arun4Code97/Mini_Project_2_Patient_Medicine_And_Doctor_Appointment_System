package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public List<Appointment> collectBookedSlots(Long doctorId, LocalDate appointmentDate){
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(() -> new RuntimeException("Doctor Id not found"));
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId,appointmentDate);
    }
    public Appointment bookAppointment(Long doctorId, Long patientId, LocalDate appointmentDate, LocalTime appointmentTime){
       Doctor doctor = doctorRepository.findById(doctorId).get();
       Patient patient = patientRepository.findById(patientId).get();
        Appointment savedAppointment = new Appointment(
                null,
                doctor,
                patient,
                appointmentDate,
                appointmentTime,
                true);
        appointmentRepository.save(savedAppointment);
        return savedAppointment;
    }
}
