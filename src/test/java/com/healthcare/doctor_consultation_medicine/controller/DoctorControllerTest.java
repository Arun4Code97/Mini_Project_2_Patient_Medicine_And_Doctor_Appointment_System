package com.healthcare.doctor_consultation_medicine.controller;

import com.healthcare.doctor_consultation_medicine.Controller.DoctorController;
import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Exception.DoctorNotFoundException;
import com.healthcare.doctor_consultation_medicine.Exception.GlobalExceptionHandler;
import com.healthcare.doctor_consultation_medicine.Exception.PatientNotFoundException;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
@Import(GlobalExceptionHandler.class)
@WebMvcTest(controllers = DoctorController.class)
//@SpringBootTest
//@AutoConfigureMockMvc
public class DoctorControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DoctorService doctorService;
    @InjectMocks
    private DoctorController doctorController;
    private List<MedicineDto> medicineList;
    private DoctorDto doctorDto;
    private MockMultipartFile mockImageFile;
    private static MultiValueMap<String, String> convertToParams(DoctorDto tempDoctorDto) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", tempDoctorDto.getId());
        params.add("firstName", tempDoctorDto.getFirstName());
        params.add("lastName", tempDoctorDto.getLastName());
        params.add("gender", tempDoctorDto.getGender());
        params.add("email", tempDoctorDto.getEmail());
        params.add("specialization", tempDoctorDto.getSpecialization());
        params.add("qualification", tempDoctorDto.getQualification());
        params.add("experience", tempDoctorDto.getExperience());
        params.add("phone", tempDoctorDto.getPhone());

        return params;
    }
    @BeforeEach()
    public void setUp(){
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new DoctorController(doctorService)).build();

        doctorDto = DoctorDto.builder()
                .id("1")
                .firstName("Dr. Ajaya")
                .lastName("Nand Jha")
                .gender("Male")
                .email("doctor1@gmail.com")
                .specialization("Neurosurgery")
                .qualification("MBBS,MS,FRCS")
                .experience("42")
                .phone("9876543210")
                .build();
        mockImageFile = new MockMultipartFile(
                "imageFile",          // form field name
                "doctorImage.jpg",     // original filename
                "image/jpeg",          // content type
                "image content".getBytes());  // file content
    }
    @DisplayName("Junit test for toHandleAddDoctorGetRequest GET method - positive case")
    @Test
    public void toHandleAddDoctorGetRequest() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/addDoctor"));
        //  Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("doctor/addDoctor"));
        resultActions.andExpect(model().attributeExists("doctor","mode","showDoctorForm"));

//        // Optional: Print the response body for debugging
//        System.out.println(resultActions.andReturn().getResponse().getContentAsString());
    }
    @DisplayName("Junit test for toHandleAddPatientRequest GET method - negative case - with wrong URL")
    @Test
    public void testToHandleAddPatientRequest_wrongUrl() throws Exception {
        mockMvc.perform(get("/hospital/addDoctorWrongUrl"))
                .andExpect(status().isNotFound()); // Expecting a 404 response
//                .andDo(print());
    }
    @DisplayName("Junit test for toHandleAddDoctorRequest POST method - Positive case")
    @Test
    public void testToHandleAddDoctor_Success() throws Exception {
        // Arrange
        // Email does not exist, So proceed for saving as new
        when(doctorService.isExistByEmail(anyString())).thenReturn(false);
        when(doctorService.saveDoctorWithImage(any(DoctorDto.class))).thenReturn(doctorDto);

        // Act & Assert
        MvcResult result = mockMvc.perform(multipart("/hospital/addDoctor")
                        .file(mockImageFile)
                        .params(convertToParams(doctorDto)) // Simulate form parameters
                       )
//                .andDo(print()) // Print request and response to console for debugging
                .andExpect(status().is3xxRedirection()) // Expect redirection
                .andExpect(redirectedUrl("/hospital/addDoctor/setPassword?savedDoctorId=" + doctorDto.getId())) // Check redirect URL
                .andExpect( flash().attributeExists( "setPassword"))
                .andReturn();

        // Verify the service interaction
        verify(doctorService, times(1)).isExistByEmail(anyString());
        verify(doctorService, times(1)).saveDoctorWithImage(any(DoctorDto.class));
    }
    @Test
    @DisplayName("JUnit test for handling POST /addDoctor - Validation Errors")
    public void testToHandleAddDoctorPostRequest_ValidationErrors() throws Exception {
        // Arrange
        doctorDto.setFirstName(""); // Empty email to trigger validation error
        doctorDto.setEmail("");     // Empty name to trigger validation error

        // Act & Assert
        mockMvc.perform(multipart("/hospital/addDoctor")
                        .file(mockImageFile)
                        .params(convertToParams(doctorDto))
                )
                .andExpect(status().isOk()) // Expect to stay on the form
                .andExpect(view().name("doctor/addDoctor"))
                .andExpect(model().attributeExists( "mode"))
                .andExpect(model().hasErrors()); // Validation errors exist

        // Verify that save was not called due to validation errors
        verify(doctorService, times(0)).saveDoctorWithImage(any(DoctorDto.class));
    }

    @Test
    @DisplayName("JUnit test for handling POST /addDoctor - Email Already Exists")
    public void testToHandleAddDoctorPostRequest_EmailAlreadyExists() throws Exception {
        // Arrange
        when(doctorService.isExistByEmail(anyString()))
                .thenReturn(true); // Simulate email already exists

        // Act & Assert
        mockMvc.perform(multipart("/hospital/addDoctor")
                        .file(mockImageFile)
                        .params(convertToParams(doctorDto))
                )
                .andExpect(status().isOk()) // Expect to stay on the form
                .andExpect(view().name("doctor/addDoctor")) // Expect form view
                .andExpect(model().attributeExists("errorExistEmail", "showDoctorForm", "mode")) // Check error message
                .andExpect(model().attribute("errorExistEmail", "Doctor Email ID already exist"));

        // Verify that save was not called since the email exists
        verify(doctorService, times(1)).isExistByEmail(anyString());
        verify(doctorService, never()).saveDoctorWithImage(any(DoctorDto.class));
    }
    @DisplayName("Junit test for toHandleSetPasswordRequest GET method - positive case")
    @Test
    public void testToHandleSetPasswordRequest() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/addDoctor/setPassword")
                .param("savedDoctorId", String.valueOf(1L)) );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("doctor/addDoctor"));
        resultActions.andExpect(model().attributeExists("mode","credentials"
                ,"savedDoctorId","setPassword"));
        resultActions.andExpect(model().attribute("savedDoctorId",1L));
        resultActions.andExpect(model().attributeDoesNotExist("showPatientForm")); // Ensure showPatientForm fragment disabled
    }
    @DisplayName("Junit test for toHandleSetPasswordRequest GET method - negative case with wrong Url")
    @Test
    public void testToHandleSetPasswordRequest_invalidUrl() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/addDoctor/setPasswordWrongUrl")
                .param("savedDoctorId", String.valueOf(1L)) );
        // Assert
        resultActions.andExpect(status().isNotFound() ); // As given wrongUrl
    }
    @DisplayName("JUnit test for toHandleSetPasswordRequest GET method - negative case with invalid parameter")
    @Test
    public void testToHandleSetPasswordRequest_invalidParameter() throws Exception {
        // Act & Assert: Invalid parameter (non-numeric savedPatientId)
        mockMvc.perform(get("/hospital/addDoctor/setPassword")
                        .param("savedDoctorId", "invalidId")) // Invalid parameter
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request for invalid parameter

        // Act & Assert: Missing required parameter (savedPatientId)
        mockMvc.perform(get("/hospital/addDoctor/setPassword"))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request for missing parameter
    }
    @DisplayName("Junit test for toHandleSetPasswordRequest POST method - Positive case")
    @Test
    public void testToHandleSetPasswordRequest_success() throws Exception {

        // Act & Assert
        mockMvc.perform(post("/hospital/addDoctor/setPassword")
                        .param("password","password@123")
                        .param("confirmPassword","password@123")
                        .param("savedDoctorId",String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success",true));
        // verify
        verify(doctorService, times(1))
                .setPassword(anyLong(),anyString());
    }

    @DisplayName("JUnit test for toHandleSetPasswordRequest POST method - Negative case with Validation Errors")
    @Test
    public void testToHandleSetPasswordRequest_validationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/hospital/addDoctor/setPassword")
                        .param("password","password@123")
                        .param("confirmPassword","")  // invalid confirmPassword param
                        .param("savedDoctorId",String.valueOf(1L))
                ).andExpect(status().isOk())  // Expecting the form to reload (not redirect)
                .andExpect(view().name("doctor/addDoctor"))  // Form should be displayed again
                .andExpect(model().attribute("setPassword", "fragmentName"))
                .andExpect(model().attributeExists("mode","savedDoctorId"))
                .andExpect(model().attributeDoesNotExist("error", "success")); // Ensure there is no existMail and success logic executed
        // verify
        verify(doctorService, never())
                .setPassword(anyLong(),anyString());
    }
    @DisplayName("JUnit test for toHandleSetPasswordRequest POST method - password mismatch")
    @Test
    public void testToHandleAddDoctor_passwordMismatch() throws Exception {

        // Act & Assert
        mockMvc.perform(post("/hospital/addDoctor/setPassword")
                        .param("password","password@123")
                        .param("confirmPassword","misMatchPassword")  // mismatching confirmPassword param
                        .param("savedDoctorId",String.valueOf(1L))
                ).andExpect(status().isOk())  // Expecting the form to reload (not redirect)
                .andExpect(view().name("doctor/addDoctor"))  // Form should be displayed again with error message
                .andExpect(model().attributeExists("mode","savedDoctorId","setPassword","error"))
                .andExpect(model().attribute("error","Passwords are not matching"))
                .andExpect(model().attributeDoesNotExist("success"));// Ensure there is no success logic executed
        // verify
        verify(doctorService, never())
                .setPassword(anyLong(),anyString());
    }
    @DisplayName("Junit test for toHandleDoctorViewProfileRequest GET method - positive case")
    @Test
    public void toHandleDoctorViewProfileRequest_success() throws Exception {
        // Arrange
        doctorDto.setImage("imageFile".getBytes());
        when(doctorService.getSingleDoctorById(anyLong()))
                .thenReturn(doctorDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/doctorPortal/1") );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("doctor/doctorPortal"));
        resultActions.andExpect(model().attributeExists("mode","doctor","showDoctorForm","doctorBase64Image"));
    }
    @DisplayName("Junit test for toHandleDoctorViewProfileRequest GET method - negative case with wrong url")
    @Test
    public void toHandlePatientViewProfileRequest_wrongUrl() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/doctorPortalss/" + 1) ); // wrong Url
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandleDoctorViewProfileRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toHandleDoctorViewProfileRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/doctorPortal/invalidPathStringValue") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @DisplayName("JUnit test for toHandleDoctorViewProfileRequest GET method - Negative case with non-existing Doctor ID")
    @Test
    public void toHandleDoctorViewProfileRequest_notExistDoctorId() throws Exception {
        // Arrange
        long nonExistentDoctorId = 99L;
        when(doctorService.getSingleDoctorById(anyLong()))
                .thenThrow(new DoctorNotFoundException(
                        "Doctor with ID: " + nonExistentDoctorId + " does not exist"));
        // Act & Assert
        mockMvc.perform(get("/hospital/doctorPortal/{id}", nonExistentDoctorId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        DoctorNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Doctor with ID: 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());
    }

    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - positive case")
    @Test
    public void toHandleUpdateProfileRequest_success() throws Exception {
        // Arrange
        when(doctorService.getSingleDoctorById(anyLong()))
                .thenReturn(doctorDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform( get("/hospital/doctorPortal/updateProfile/1") );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("doctor/doctorPortal"));
        resultActions.andExpect(model().attributeExists("mode","doctor","showDoctorForm"));
    }

    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - negative case with wrong url")
    @Test
    public void toHandleUpdateProfileRequest_wrongUrl() throws Exception {
        // Arrange
        when(doctorService.getSingleDoctorById(anyLong()))
                .thenReturn(doctorDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform( get("/hospital/doctorPortal/wrongUrl/1") );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toHandleUpdateProfileRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform( get(
                "/hospital/doctorPortal/updateProfile/invalidPathVariableType") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - negative case with nonExistPatientId")
    @Test
    public void toHandleUpdateProfileRequest_notExistPatientId() throws Exception {
        // Arrange
        long nonExistentDoctorId = 99L;
        when(doctorService.getSingleDoctorById(anyLong()))
                .thenThrow(new DoctorNotFoundException(
                        "Doctor with ID: " + nonExistentDoctorId + " does not exist"));
        // Act & Assert
        mockMvc.perform(get("/hospital/doctorPortal/updateProfile/{id}", nonExistentDoctorId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        DoctorNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Doctor with ID: 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());
    }
    @DisplayName("JUnit test for toHandleUpdateProfile PUT method - Positive Case")
    @Test
    public void testToHandleUpdateProfile_Success() throws Exception {
        // Arrange
        long doctorId = 1L;
        doctorDto.setEmail("doctorupdated@gmail.com");

        when( doctorService.saveDoctorWithImage(any(DoctorDto.class))).thenReturn(doctorDto);

        // Act & Assert
        mockMvc.perform(multipart("/hospital/doctorPortal/updateProfile/"+ doctorId)
                        .file(mockImageFile)
                        .params(convertToParams(doctorDto))
                        .with(request -> {
                            request.setMethod("PUT");  // change HTTP method to PUT
                            return request;
                        })
                )
                .andDo(print())// Simulate form submission
                .andExpect(status().is3xxRedirection())  // Check for redirection
                .andExpect(redirectedUrl("/hospital/doctorPortal/" + doctorId))  // Check correct redirection URL
                .andExpect(flash().attribute("updatedDoctor"
                        , hasProperty("email", is("doctorupdated@gmail.com"))))
        ;  // Verify the updated email

        // Verify that the service was called
        verify(doctorService, times(1))
                .saveDoctorWithImage(any(DoctorDto.class));
    }
    @DisplayName("JUnit test for toHandleUpdateProfile PUT method - Negative Case: Validation Error")
    @Test
    public void testToHandleUpdateProfile_ValidationError() throws Exception {
        // Arrange
        long doctorId = 1L;

        // Set invalid data in doctorDto, for example, an invalid email format or missing required fields
        doctorDto.setEmail("invalid-email");  // Invalid email format
        doctorDto.setFirstName("");           // First name is required, but missing

        // Act & Assert
        mockMvc.perform(multipart("/hospital/doctorPortal/updateProfile/" + doctorId)
                        .file(mockImageFile)  // attach the file
                        .params(convertToParams(doctorDto))  // pass the invalid params
                        .with(request -> {
                            request.setMethod("PUT");  // change HTTP method to PUT
                            return request;
                        })
                )
//                .andDo(print())  // print request/response details
                .andExpect(status().isOk())  // We expect a 200 status since it will return the form with validation errors
                .andExpect(view().name("doctor/doctorPortal"))  // Ensure the form view is returned
/* Note */      .andExpect(model().attributeHasFieldErrors("doctor", "email", "firstName"))  // Check for field errors
                .andExpect(model().attributeExists("mode","showDoctorForm"))
                .andExpect(model().attributeDoesNotExist("success","updatedDoctor"))  // Ensure these attributes not present as unsuccessful update request
                 ;

        // Verify that the service is not called because validation fails
        verify(doctorService, times(0)).saveDoctorWithImage(any(DoctorDto.class));
    }
    @DisplayName("Junit test for toHandleDeleteDoctorGetRequest GET method - positive case")
    @Test
    public void toHandleDeleteDoctorGetRequest_success() throws Exception {
        // Arrange
        when(doctorService.isExistById(anyLong()))
                .thenReturn(true);  // Mock service method behaviour
        when(doctorService.getSingleDoctorById(anyLong())).thenReturn(doctorDto);
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/doctorPortal/deleteDoctor/1") );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("doctor/doctorPortal"));
        resultActions.andExpect(model().attributeExists("mode","doctor","deleteDoctor"));
        resultActions.andExpect(model().attributeDoesNotExist("error"));
    }
    @DisplayName("Junit test for toHandleDeleteDoctorGetRequest GET method - negative case with wrong url")
    @Test
    public void toHandleDeleteDoctorGetRequest_wrongUrl() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/doctorPortal/deleteDoctorWrongUrl/1") );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandleDeleteDoctorGetRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toHandleDeleteDoctorGetRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/doctorPortal/deleteDoctor/"+ "inValidPathVariable") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @DisplayName("Junit test for toHandleDeleteDoctorGetRequest GET method - negative case with nonExistPatientId")
    @Test
    public void toHandleDeleteDoctorGetRequest_notExistPatientId() throws Exception {
        // Arrange
        long nonExistentDoctorId = 99L;
        when(doctorService.getSingleDoctorById(anyLong()))
                .thenThrow(new DoctorNotFoundException("Doctor Id : " + nonExistentDoctorId + " does not exist"
                ));
        // Act & Assert
        mockMvc.perform(get("/hospital/doctorPortal/deleteDoctor/{id}", nonExistentDoctorId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        DoctorNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Doctor Id : 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());
    }
    @DisplayName("JUnit test for toHandleDeleteDoctorDeleteRequest DELETE method - Positive Case")
    @Test
    public void toHandleDeleteDoctorDeleteRequest_Success() throws Exception {
        // Arrange
        Long patientId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/hospital/doctorPortal/deleteDoctor/{id}", patientId) )
//                .andDo(print())// Simulate form submission
                .andExpect(status().is3xxRedirection())  // Check for redirection
                .andExpect(redirectedUrl("/hospital"));

        // Verify that the service was called
        verify(doctorService, times(1))
                .deleteDoctorById(eq(patientId));
    }
    @DisplayName("Junit test for toHandleDeleteDoctorDeleteRequest DELETE method - negative case with wrong url")
    @Test
    public void toHandleDeleteDoctorDeleteRequest_wrongUrl() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                delete("/hospital/doctorPortal/deleteDoctorWrongUrl/{id}",1) );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
}
