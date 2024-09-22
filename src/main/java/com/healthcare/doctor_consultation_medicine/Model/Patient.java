package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private Integer age;
    private String gender;

    private String email;

    private Long phone;
    private Float height;
    private Float weight;
    private String city;

    private String address;
    private String medicalHistory;

    // One patient can have multiple prescribed medicines
    @OneToMany(mappedBy = "patient")
    private List<Medicine> medicines = new ArrayList<>();

    private String emergencyContactRelationship;

    private Long emergencyContactNumber;
}
