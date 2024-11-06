package com.healthcare.doctor_consultation_medicine.controller;

import com.healthcare.doctor_consultation_medicine.Controller.PatientController;
import com.healthcare.doctor_consultation_medicine.Exception.GlobalExceptionHandler;
import com.healthcare.doctor_consultation_medicine.Exception.PatientNotFoundException;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PatientController.class) // Load only PatientController
@Import(GlobalExceptionHandler.class) // Import the exception handler
public class PatientNotFoundExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService; // Mock PatientService
    @MockBean
    private DoctorService doctorService;

    @Test
    @DisplayName("Test PatientNotFoundException handling for non-existent patient ID")
    public void testGetPatientProfile_PatientNotFound() throws Exception {
        // Arrange
        long nonExistentPatientId = 99L;

        // Mock the patientService to throw PatientNotFoundException for the ID
        when(patientService.getSinglePatientById(nonExistentPatientId))
                .thenThrow(new PatientNotFoundException("Patient with ID: " + nonExistentPatientId + " does not exist"));

        // Act & Assert
        mockMvc.perform(get("/hospital/patientPortal/{id}", nonExistentPatientId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        PatientNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals(
                        "Patient with ID: 99 does not exist",
                        result.getResolvedException().getMessage()))
                .andDo(print());
    }
}