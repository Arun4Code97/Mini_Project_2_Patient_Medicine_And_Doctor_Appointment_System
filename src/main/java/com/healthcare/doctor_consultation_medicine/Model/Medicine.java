package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String dosage;
    private String duration;
    // Many medicines can be prescribed by one doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Many medicines can be assigned to one patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

}
