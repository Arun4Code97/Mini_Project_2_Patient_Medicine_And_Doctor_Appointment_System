package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "medicines") // Exclude medicines to prevent circular reference
@Builder
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String password;

    private Integer age;
    private String gender;

    @Column(unique = true,nullable = false)
    private String email;

    private Long phone;
    private Float height;
    private Float weight;
    private String city;

    private String address;
    private String medicalHistory;

    // One patient can have multiple prescribed medicines
    @Builder.Default
    @OneToMany(mappedBy = "patient")
    private List<Medicine> medicines = new ArrayList<>();

    private String emergencyContactRelationship;

    private Long emergencyContactNumber;
}
