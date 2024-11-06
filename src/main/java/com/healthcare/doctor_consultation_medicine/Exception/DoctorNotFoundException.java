package com.healthcare.doctor_consultation_medicine.Exception;

public class DoctorNotFoundException extends RuntimeException{
    public DoctorNotFoundException(String message) {
        super(message);
    }
}
