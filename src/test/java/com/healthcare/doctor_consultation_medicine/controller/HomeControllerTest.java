package com.healthcare.doctor_consultation_medicine.controller;

import com.healthcare.doctor_consultation_medicine.Controller.HomeController;
import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.DTO.UserLoginDTO;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.print.Doc;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
public class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    PatientService patientService;
    @MockBean
    DoctorService doctorService;
    @InjectMocks
    HomeController homeController;
    UserLoginDTO validUser_Patient,validUser_Doctor;
    PatientDto patientDto;
    DoctorDto doctorDto;
    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(
                new HomeController(patientService,doctorService)).build();
        validUser_Patient = new UserLoginDTO(
                "patient", "patient1@gmail.com", "password");
        validUser_Doctor = new UserLoginDTO(
                "doctor", "doctor1@gmail.com", "password");

        patientDto = PatientDto.builder()
                .id("1")
                .firstName("Alexander")
                .lastName("GrahamBell")
                .age("23")
                .gender("Male")
                .email("patient1@gmail.com")
                .password("password")
                .phone("9876543210")
                .height("172.35")
                .weight("80.5")
                .city("New York")
                .address("Door No -10,2nd Street,Manhattan,New York,America")
                .medicalHistory("No Diabetics, No Hyperthermia, Undergone byPass heart surgery 10 years ago")
                .emergencyContactRelationship("Son")
                .emergencyContactNumber("9876543210")
                .build();
        doctorDto = DoctorDto.builder()
                .id("1")
                .firstName("Dr. Ajaya")
                .lastName("Nand Jha")
                .gender("Male")
                .email("doctor1@gmail.com")
                .password("password")
                .specialization("Neurosurgery")
                .qualification("MBBS,MS,FRCS")
                .experience("42")
                .phone("9876543210")
                .build();
    }

    @DisplayName("JUnit test for toHandleHomePageRequest - Positive Case")
    @Test
    public void testToHandleHomePageRequest_Success() throws Exception {
        mockMvc.perform(get("/hospital"))
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))
                .andExpect(model().attributeExists("userData"))  // Verify that model has "userData" attribute
                .andExpect(model().attribute("userData",
                        hasProperty("userType", is("patient"))));  // Verify default value of userType is "patient"
    }
    @DisplayName("JUnit test for toHandleHomePageRequest - negative Case (wrong url)")
    @Test
    public void testToHandleHomePageRequest_WrongUrl() throws Exception {
        mockMvc.perform(get("/hospitals")) //hospital only exist
                .andExpect(status().isNotFound());
    }


    @DisplayName("JUnit test for toHandleLoginRequest - Positive Case (Patient Login)")
    @Test
    public void testToHandleLoginRequest_PatientLoginSuccess() throws Exception {
        when(patientService.findPatientByEmailId(anyString())).thenReturn(Optional.of(patientDto));
        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", validUser_Patient))  // Using a valid user with type "patient"
                .andExpect(status().is3xxRedirection())  // Check for a successful redirection
                .andExpect(redirectedUrl("/hospital/patientPortal/1"))
                .andDo(print());
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Negative Case (Validation Errors)")
    @Test
    public void testToHandleLoginRequest_WithValidationErrors() throws Exception {
        UserLoginDTO invalidUser = new UserLoginDTO(
                "", "patient1@gmail.com", null);  // Missing userType & password to trigger validation error

        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", invalidUser))  // Pass the invalid user object
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))  // Should return to the home page
                .andExpect(model().attributeHasFieldErrors("userData",  "userType","password"));  // Check for specific field errors
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Negative Case (invalid userType)")
    @Test
    public void testToHandleLoginRequest_InvalidUserType() throws Exception {
        UserLoginDTO invalidUser = new UserLoginDTO(
                "invalidUserType", "patient1@gmail.com", "password");  // incorrect userType ,which should be either patient or doctor

        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", invalidUser))  // Pass the invalid user object
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))  // Should return to the home page
                .andExpect(model().attribute("error",  "Invalid user type."));  // Check for specific field errors
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Negative Case (patient email not found)")
    @Test
    public void testToHandleLoginRequest_patientEmailNotFound() throws Exception {
        //Arrange
        when(patientService.findPatientByEmailId(anyString())).thenReturn(Optional.empty());

        UserLoginDTO invalidUser = new UserLoginDTO(
                "patient", "patient1@gmail.com", "password");
        //Act & Assert
        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", invalidUser))  // Pass the invalid user object
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))  // Should return to the home page
                .andExpect(model().attribute("error",  "Email ID " + invalidUser.getEmail() + " does not exist."));  // Check for specific field errors
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Negative Case (not matching patient password)")
    @Test
    public void testToHandleLoginRequest_patientPasswordMismatch() throws Exception {
        //Arrange
        when(patientService.findPatientByEmailId(anyString())).thenReturn( Optional.of(patientDto) ); // Here, passwordField is password

        UserLoginDTO invalidUser = new UserLoginDTO(
                "patient", "patient1@gmail.com", "wrongPassword"); // wrongPassword
        //Act & Assert
        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", invalidUser))  // Pass the invalid user object
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))  // Should return to the home page
                .andExpect(model().attribute("error",  "Incorrect password."));
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Positive Case (Doctor Login)")
    @Test
    public void testToHandleLoginRequest_DoctorLoginSuccess() throws Exception {
        when(doctorService.findDoctorByEmailId(anyString())).thenReturn(Optional.of(doctorDto));
        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", validUser_Doctor))  // Using a valid user with type "doctor"
                .andExpect(status().is3xxRedirection())  // Check for a successful redirection
                .andExpect(redirectedUrl("/hospital/doctorPortal/1"))
                .andDo(print());
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Negative Case (doctor email not found)")
    @Test
    public void testToHandleLoginRequest_doctorEmailNotFound() throws Exception {
        //Arrange
        when(doctorService.findDoctorByEmailId(anyString())).thenReturn(Optional.empty()); // Return empty

        UserLoginDTO invalidUser = new UserLoginDTO(
                "doctor", "doctor1@gmail.com", "password");
        //Act & Assert
        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", invalidUser))  // Pass the invalid user object
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))  // Should return to the home page
                .andExpect(model().attribute("error",  "Email ID " + invalidUser.getEmail() + " does not exist."));  // Check for specific field errors
    }
    @DisplayName("JUnit test for toHandleLoginRequest - Negative Case (not matching doctor password)")
    @Test
    public void testToHandleLoginRequest_doctorPasswordMismatch() throws Exception {
        //Arrange
        when(doctorService.findDoctorByEmailId(anyString())).thenReturn( Optional.of(doctorDto) );

        UserLoginDTO invalidUser = new UserLoginDTO(
                "doctor", "patient1@gmail.com", "wrongPassword"); // given wrongPassword
        //Act & Assert
        mockMvc.perform(post("/hospital/login")
                        .flashAttr("userData", invalidUser))  // Pass the invalid user object
                .andExpect(status().isOk())
                .andExpect(view().name("common/homePage"))  // Should return to the home page
                .andExpect(model().attribute("error",  "Incorrect password."));
    }
}
