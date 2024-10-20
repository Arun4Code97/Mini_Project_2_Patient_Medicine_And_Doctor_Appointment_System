package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient ;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Column(columnDefinition = "TIME",nullable = false) // Ensures it is stored as TIME in MySQL without fractional seconds
    private LocalTime appointmentTime;

    private boolean isBooked;
    private String doctorAdvice;

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", doctorId=" + doctor.getId() +
                ", patientId=" + patient.getId() +
                ", date=" + appointmentDate +
                ", time=" + appointmentTime +
                ", isBooked=" +isBooked +
                "}";
    }
}
