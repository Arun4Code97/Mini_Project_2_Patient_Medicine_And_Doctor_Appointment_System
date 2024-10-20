package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Mapper.PatientMapper;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    @Override
    public void addAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }
    @Override
    public Appointment findById(Long appointmentId) {
        Optional<Appointment> appointment = appointmentRepository.findById(appointmentId);
        return appointment.orElse(null);// Return the appointment if present, otherwise null
    }

    @Override
    public PatientDto fetchPatientRecord(Long doctorId, LocalDate date, LocalTime time){
        Appointment appointment = appointmentRepository.
                findByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId,date,time);
        Patient patient = appointment.getPatient();
        return PatientMapper.toMapPatientDto(patient);
    }
    @Override
    public Appointment fetchAppointment(Long doctorId, LocalDate date, LocalTime time) {
        return appointmentRepository.
                findByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId,date,time);
    }

    @Override
    public List<Appointment> collectBookedSlots(Long doctorId, LocalDate appointmentDate){
        return appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId, appointmentDate);
    }
    @Override
    public Appointment bookAppointment(Long doctorId, Long patientId, LocalDate appointmentDate, LocalTime appointmentTime){
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        Patient patient = patientRepository.findById(patientId).orElse(null);
        Appointment savedAppointment = new Appointment(
                null,
                doctor,
                patient,
                appointmentDate,
                appointmentTime,
                true,
                null);
        return appointmentRepository.save(savedAppointment);
    }
}
