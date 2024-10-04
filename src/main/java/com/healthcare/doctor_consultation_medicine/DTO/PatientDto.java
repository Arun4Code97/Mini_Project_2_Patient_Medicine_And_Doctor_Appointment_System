package com.healthcare.doctor_consultation_medicine.DTO;


import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "medicines")
public class PatientDto {

    private String id;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ,.'-]{1,29}$", message = "Enter Valid inputs: {A-Z,a-z., '-}")
    private String firstName;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ,.'-]{1,29}$", message = "Enter Valid inputs: {A-Z,a-z., '-}")
    private String lastName;

    private String password;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[0-9]{1,3}$", message = "Enter valid number up to 3 digits")
    private String age;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]{1,15}$", message = "Enter Valid inputs: {A-Z,a-z }")
    private String gender;

    @Email(message = "Enter Valid email Id")
    @NotBlank(message = "Field should not be blank")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Should start with above 6 and contain exactly 10 digits.")
    @NotBlank(message = "Field should not be blank")
    private String phone;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[0-9]{1,3}(\\.[0-9]{1,2})?$" , message = "Valid inputs: {103.25,10.26,60.1}" )
    private String height;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[0-9]{1,3}(\\.[0-9]{1,2})?$" , message = "Valid inputs: {103.25,175.26,60.1}" )
    private String weight;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]{1,15}$", message = "Alphabets characters allowed and max 15")
    private String city;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[A-Za-z0-9\\-.,:/()#& ]{1,255}$", message = "Allowed characters (A-Z,a-z,0-9), (-, ./:()#&)  and up to 256 characters.")
    private String address;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[A-Za-z0-9\\-.,:/()#& ]{1,1024}$", message = "Allowed characters (A-Z,a-z,0-9) , (-, ./:()#&) and up to 256 characters.")
    private String medicalHistory;

    private List<MedicineDto> medicines = new ArrayList<>();

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ]{1,15}$", message = "Alphabets characters allowed and max 15")
    private String emergencyContactRelationship;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Should start with above 6 and contain exactly 10 digits.")
    private String emergencyContactNumber;
}
