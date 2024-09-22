package com.healthcare.doctor_consultation_medicine.DTO;


import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class PatientDto {

    private String id;

    @NotBlank(message = "Patient first name can not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ,.'-]{1,29}$", message = "Invalid input: It must start with a letter and contain 2 to 30 characters, including letters and special characters(, .'-)")
    private String firstName;

    @NotBlank(message = "Patient last name can not be blank")
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z ,.'-]{1,29}$", message = "Invalid input: It must start with a letter and contain 2 to 30 characters, including letters and special characters(, .'-)")
    private String lastName;

    @NotBlank(message = "Patient age can not be blank")
    @Pattern(regexp = "^[0-9]{1,3}$", message = "Invalid input : Only numbers are allowed with up to 3 digits max")
    private String age;

    @NotBlank(message = "Patient gender can not be blank")
    @Pattern(regexp = "^[a-zA-Z]{1,15}$", message = "Invalid input: It should contain 1 to 15 alphabets characters")
    private String gender;

    @Email(message = "Patient email ID must be valid ")
    @NotBlank(message = "Patient email ID must not be blank")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid input: It should start with 6, 7, 8, or 9 and contain exactly 10 digits.")
    @NotBlank(message = "Patient phone number must not be blank")
    private String phone;

    @NotBlank(message = "Patient height must not be blank")
    @Pattern(regexp = "^[0-9]{1,3}(\\.[0-9]{1,2})?$" , message = "Invalid input: It must be a number with up to 3 digits and if .(Dot) present, 1 or 2 digits must be after it" )
    private String height;

    @NotBlank(message = "Patient weight must not be blank")
    @Pattern(regexp = "^[0-9]{1,3}(\\.[0-9]{1,2})?$" , message = "Invalid input: It must be a number with up to 3 digits and if .(Dot) present, 1 or 2 digits must be after it" )
    private String weight;

    @NotBlank(message = "Patient city must not be blank")
    @Pattern(regexp = "^[a-zA-Z]{1,15}$", message = "Invalid input: It should contain 1 to 15 alphabets characters")
    private String city;

    @NotBlank(message = "Patient address must not be blank")
    @Pattern(regexp = "^[A-Za-z0-9\\-.,:/()#& ]{1,255}$", message = "Invalid input: Allowed characters (A-Z,a-z,0-9), (-, ./:()#&)  and up to 256 characters.")
    private String address;

    @NotBlank(message = "Patient medical history must not be blank")
    @Pattern(regexp = "^[A-Za-z0-9\\-.,:/()#& ]{1,1024}$", message = "Invalid input: Allowed characters (A-Z,a-z,0-9) , (-, ./:()#&) and up to 256 characters.")
    private String medicalHistory;

    private List<MedicineDto> medicines = new ArrayList<>();

    @NotBlank(message = "Patient relation type must not be blank")
    @Pattern(regexp = "^[a-zA-Z]{1,15}$", message = "Invalid input: It should contain 1 to 15 alphabets characters")
    private String emergencyContactRelationship;

    @NotBlank(message = "Patient emergency contact must not be blank")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid input : It should start with 6, 7, 8, or 9 and contain exactly 10 digits.")
    private String emergencyContactNumber;
}
