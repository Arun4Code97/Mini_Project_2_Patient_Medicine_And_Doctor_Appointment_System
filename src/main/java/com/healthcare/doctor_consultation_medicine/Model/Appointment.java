package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    private LocalDate appointmentDate;
    @Column(columnDefinition = "TIME") // Ensures it is stored as TIME in MySQL without fractional seconds
    private LocalTime appointmentTime;
    private boolean isBooked;

//    @Override
//    public String toString() {
//        return "Appointment{" +
//                "id=" + id +
//                ", doctorId=" + doctor.getId() +
//                ", patientId=" + patient.getId() +
//                ", date=" + appointmentDate +
//                ", time=" + appointmentTime +
//                ", isBooked=" +isBooked +
//                '}';
//    }
}
