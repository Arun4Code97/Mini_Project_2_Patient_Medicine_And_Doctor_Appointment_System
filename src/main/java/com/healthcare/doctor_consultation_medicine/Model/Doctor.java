package com.healthcare.doctor_consultation_medicine.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = {"image","medicines","appointments"}) // Excluded to prevent circular reference
@Builder
public class Doctor {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
@Column(nullable = false)
private String firstName;
@Column(nullable = false)
private String lastName;
private String password;
private String gender;

@Column(unique = true,nullable = false)
private String email;

private String specialization;
private String qualification;
private Integer experience;
private Long phoneNumber;

@Builder.Default
@Lob
@Column(name = "image" ,columnDefinition = "MEDIUMBLOB")
private byte[] image = null;

// One doctor can prescribe multiple medicines
@Builder.Default
@OneToMany(mappedBy = "doctor")
private List<Medicine> medicines = null;

@Builder.Default
@OneToMany(mappedBy = "doctor")
private List<Appointment> appointments = null;
}
