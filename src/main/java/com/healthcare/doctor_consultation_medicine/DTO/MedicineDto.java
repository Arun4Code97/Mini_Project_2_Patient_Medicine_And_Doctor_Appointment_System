package com.healthcare.doctor_consultation_medicine.DTO;

import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedicineDto {
    @NotBlank(message = "Id should not be blank")
    @Pattern(regexp = "^[0-9]{1,2}$" ,message="Should contain up to 2 digit valid number ")
    private String id;

    @NotBlank(message = "Specialization should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9 .,'-/]{1,29}$", message = "Should start with a letter and contain up to 30 characters, including numbers,letters and special characters(, .'-)")
    private String name;

    @NotBlank(message = "Specialization should not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9 .,'-/]{1,29}$", message = "Should start with a letter and contain up to 30 characters, including letters,numbers and special characters(, .'-)")
    private String dosage;

    @NotBlank(message = "Specialization should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9 .,'-/]{1,29}$", message = "Should start with a letter and contain up to 30 characters, including letters,numbers and special characters(, .'-)")
    private String duration;

    private Doctor doctor;
    private Patient patient;
    @Override
    public String toString(){
        return "MedicineDto{ " +
                "Id = " + id +
                ", Name = " + name +
                ", Dosage = " + dosage +
                ", Duration = " + duration + "}";
    }
}
