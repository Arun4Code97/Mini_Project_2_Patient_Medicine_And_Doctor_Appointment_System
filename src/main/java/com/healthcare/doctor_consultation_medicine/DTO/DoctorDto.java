package com.healthcare.doctor_consultation_medicine.DTO;

import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {
    private String id;

    @Pattern(regexp = "^[a-zA-Z][a-zA-Z .,'-]{1,29}$", message = "Should start with a letter and contain 2 to 30 characters(A-Za-z, .'-).")
    @NotBlank(message = "First name should not be blank.")
    private String firstName;


    @Pattern(regexp = "^[a-zA-Z][a-zA-Z .,'-]{1,29}$", message = "Should start with a letter and contain 2 to 30 characters(A-Za-z, .'-).")
    @NotBlank(message = "Last name should not be blank.")
    private String lastName;

    private String password;


    @Pattern(regexp = "^[a-zA-Z]{1,15}$", message = "1 to 15 alphabets characters only allowed.")
    @NotBlank(message = "Gender should not be blank.")
    private String gender;

    @Email(message = "email ID should be valid. ")
    @NotBlank(message = "email ID should not be blank.")
    private String email;


    @Pattern(regexp = "^[a-zA-Z][a-zA-Z .,'-]{1,29}$", message = "Should start with a letter and contain 2 to 30 characters(A-Za-z, .'-).")
    @NotBlank(message = "Specialization should not be blank.")
    private String specialization;


    @Pattern(regexp = "^[a-zA-Z][a-zA-Z .,'-]{1,29}$", message = "Should start with a letter and contain 2 to 30 characters(A-Za-z, .'-).")
    @NotBlank(message = "Qualification should not be blank.")
    private String qualification;


    @Pattern(regexp = "^[0-9]{1,2}$" , message ="Should be a 2 digit number.")
    @NotBlank(message = "Experience should not be blank.")
    private String experience;


    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Should start with above 5, and must contain exactly 10 digits.")
    @NotBlank(message = "Contact should not be blank.")
    private String phone;

    private byte[] image;

    private List<MedicineDto> medicines = new ArrayList<>();
    private List<Appointment> appointments = new ArrayList<>();
    @Override
    public String toString(){
        return "DoctorDto{" +
        "Id = " + id +
        ", Name = " + firstName + " " + lastName +
        ", Gender = " + gender +
        ", Email = " + email +
        ", Specialization = " + specialization  +
        ", Qualification = " + qualification +
        ", Experience = " +experience +
        ", Phone = " + phone + "}"   ;
    }
}
