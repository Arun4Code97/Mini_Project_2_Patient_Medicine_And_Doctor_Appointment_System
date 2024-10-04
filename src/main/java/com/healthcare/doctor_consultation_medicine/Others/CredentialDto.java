package com.healthcare.doctor_consultation_medicine.Others;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CredentialDto {
    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9 @$&*.,'-]{1,29}$", message = "letters,numbers & special characters( @$&*.,'-) allowed")
    private String password;

    @NotBlank(message = "Field should not be blank")
    @Pattern(regexp = "^[a-zA-Z0-9 @$&*.,'-]{1,29}$", message = "letters,numbers & special characters( @$&*.,'-) allowed")
    private String confirmPassword;
}
