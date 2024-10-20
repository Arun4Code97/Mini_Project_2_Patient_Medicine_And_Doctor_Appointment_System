package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"doctor", "patient"}) // Exclude circular references
@Builder
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true,nullable = false)
    private String name;
    @Column(nullable = false)
    private String dosage;
    @Column(nullable = false)
    private String duration;
    // Many medicines can be prescribed by one doctor

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    // Many medicines can be assigned to one patient
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient ;
}
