package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Doctor {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String firstName;
private String lastName;
private String gender;
private String email;
private String specialization;
//Can add doctor Speakable languages
//Doctor details or achievement as notes here
private String qualification;
private Integer experience;
private Long phoneNumber;

@Lob
@Column(name = "image" ,columnDefinition = "MEDIUMBLOB")
private byte[] image;

// One doctor can prescribe multiple medicines
@OneToMany(mappedBy = "doctor")
private List<Medicine> medicines = new ArrayList<>();

@OneToMany(mappedBy = "doctor")
private List<Appointment> appointments = new ArrayList<>();
}
