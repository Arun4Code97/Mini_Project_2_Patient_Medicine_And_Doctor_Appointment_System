package com.healthcare.doctor_consultation_medicine.controller;

import com.healthcare.doctor_consultation_medicine.Controller.DoctorController;
import com.healthcare.doctor_consultation_medicine.Controller.PatientController;
import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Exception.GlobalExceptionHandler;
import com.healthcare.doctor_consultation_medicine.Exception.PatientNotFoundException;
import com.healthcare.doctor_consultation_medicine.Mapper.MedicineMapper;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
@Import(GlobalExceptionHandler.class)
public class PatientControllerTest {
    @Autowired
    MockMvc mockMvc; // To simulate HTTP requests
    @MockBean
    PatientService patientService;
    @MockBean
    DoctorService doctorService;
    //Global variables
    private PatientDto patientDto;
    private DoctorDto doctorDto;
    private List<Medicine> medicineList;
    private static MultiValueMap<String, String> convertToParams(PatientDto tempPatientDto) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("id", tempPatientDto.getId());
        params.add("firstName", tempPatientDto.getFirstName());
        params.add("lastName", tempPatientDto.getLastName());
        params.add("age",tempPatientDto.getAge());
        params.add("gender",tempPatientDto.getGender());
        params.add("email", tempPatientDto.getEmail());
        params.add("phone", tempPatientDto.getPhone());
        params.add("height",tempPatientDto.getHeight());
        params.add("weight",tempPatientDto.getWeight());
        params.add("city",tempPatientDto.getCity());
        params.add("address",tempPatientDto.getAddress());
        params.add("medicalHistory",tempPatientDto.getMedicalHistory());
        params.add("emergencyContactRelationship",tempPatientDto.getEmergencyContactRelationship());
        params.add("emergencyContactNumber",tempPatientDto.getEmergencyContactNumber());
     return params;
    }
    @BeforeEach
    public void setup() {
//        mockMvc = MockMvcBuilders.standaloneSetup(
//                new PatientController(patientService,doctorService))
//                .build();
        patientDto = PatientDto.builder()
                .id("1")
                .firstName("Alexander")
                .lastName("GrahamBell")
                .age("23")
                .gender("Male")
                .email("alexbell@gmail.com")
                .phone("9876543210")
                .height("172.35")
                .weight("80.5")
                .city("New York")
                .address("Door No -10,2nd Street,Manhattan,New York,America")
                .medicalHistory("No Diabetics, No Hyperthermia, Undergone byPass heart surgery 10 years ago")
                .emergencyContactRelationship("Son")
                .emergencyContactNumber("9876543210")
                .build();

        medicineList = List.of(
                new Medicine(1L,"Paracetamol","10 ml","Morn-After-Night",null,null),
                new Medicine(2L, "Amoxicillin", "500 mg", "Morning and Night", null, null),
                new Medicine(3L, "Cetrizine", "5 ml", "Once at Night", null, null)
        );

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
                .medicines(medicineList.stream().map(MedicineMapper::toMapDto).collect(Collectors.toList()))
                .build();
        }

    @DisplayName("Junit test for toHandleAddPatientRequest GET method - positive case")
    @Test
    public void testToHandleAddPatientRequest() throws Exception {
        // Act: Perform a GET request to the "/hospital/addPatient" endpoint using MvcResult Interface
//       mockMvc.perform(get("/hospital/addPatient"))
//                // Assert: Verify that the status is OK (200)
//                .andExpect(status().isOk())
//                // Assert: Verify that the view returned is "patient/addPatient"
//                .andExpect(view().name("patient/addPatient"))
//                // Assert: Check that the model contains the expected attributes
//                .andExpect(model().attributeExists("patient"))
//                .andExpect(model().attributeExists("mode"))
//                .andExpect(model().attributeExists("showPatientForm"))
//                .andReturn();
        // Another method --> using ResultActions interface
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/addPatient"));
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("patient/addPatient"));
        resultActions.andExpect(model().attributeExists("patient"));
        resultActions.andExpect(model().attributeExists("mode"));
        resultActions.andExpect(model().attributeExists("showPatientForm"));

//        // Optional: Print the response body for debugging
//        System.out.println(mvcResult.getResponse().getContentAsString());
    }
    @DisplayName("Junit test for toHandleAddPatientRequest GET method - negative case - with wrong URL")
    @Test
    public void testToHandleAddPatientRequest_wrongUrl() throws Exception {
        mockMvc.perform(get("/hospital/wrongUrl"))
                .andExpect(status().isNotFound()); // Expecting a 404 response
//                .andDo(print());
    }
    @DisplayName("Junit test for toHandleAddPatientRequest POST method - Positive case")
    @Test
    public void testToHandleAddPatient_Success() throws Exception {
        // Arrange
        // Email does not exist, So proceed for saving as new
        when(patientService.existByEmailId(anyString())).thenReturn(false);
        when(patientService.addPatient(any(PatientDto.class))).thenReturn(patientDto);

        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient")
                        .params(convertToParams(patientDto)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hospital/addPatient/setPassword?savedPatientId=1"));

        verify(patientService, times(1)).addPatient(any(PatientDto.class));
    }
    @DisplayName("JUnit test for toHandleAddPatientRequest POST method - Validation Errors")
    @Test
    public void testToHandleAddPatient_ValidationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient")
                        .param("firstName", "")  // Empty field to trigger validation error
                        .param("lastName", "GrahamBell")
                        .param("email", "alexbell@gmail.com"))
                .andExpect(status().isOk())  // Expecting the form to reload (not redirect)
                .andExpect(view().name("patient/addPatient"))  // Form should be displayed again
                .andExpect(model().attribute("showPatientForm", "fragmentName"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("mode", "add"));

        verify(patientService, never()).addPatient(any(PatientDto.class));  // Service should not be called
    }

    @DisplayName("JUnit test for toHandleAddPatientRequest POST method - Positive case")
    @Test
    public void testToHandleAddPatient_success() throws Exception {
        // Arrange
        // Email does not exist, So proceed for saving as new
        when(patientService.existByEmailId(anyString())).thenReturn(false);
        when(patientService.addPatient(any(PatientDto.class))).thenReturn(patientDto);

        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient")
                        .params(convertToParams(patientDto))
                        )
                .andExpect(status().is3xxRedirection())  // Expecting a redirection status here
                .andExpect(redirectedUrl("/hospital/addPatient/setPassword?savedPatientId=1"));

        verify(patientService, times(1)).addPatient(any(PatientDto.class));
    }

    @DisplayName("JUnit test for toHandleAddPatientRequest POST method - Validation Errors")
    @Test
    public void testToHandleAddPatient_validationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient")
                        .param("firstName", "")  // Empty field to trigger validation error
                        .param("lastName", "777") // Should be alphabets
                        .param("email", "alexbell@gmail.com"))
                        // Other model object fields are null

                .andExpect(status().isOk())  // Expecting the form to reload (not redirect)
                .andExpect(view().name("patient/addPatient"))  // Form should be displayed again
                .andExpect(model().attribute("showPatientForm", "fragmentName"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("mode", "add"));

        verify(patientService, never()).addPatient(any(PatientDto.class));  // Service should not be called
    }

    @DisplayName("JUnit test for toHandleAddPatientRequest POST method - Email Exists")
    @Test
    public void testToHandleAddPatient_emailExists() throws Exception {
        // Arrange
        when(patientService.existByEmailId(anyString()))
                .thenReturn(true);  // Email exists, so reloads the same page as per my controller setUp

        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient")
                        .params(convertToParams(patientDto)) // All fields are properly passed to the method
               ).andExpect(status().isOk())  // Expecting the form to reload with same HTML document (not redirect with different document)
                .andExpect(view().name("patient/addPatient"))  // Form should be displayed again
                .andExpect(model().attribute("showPatientForm", "fragmentName"))
                .andExpect(model().attribute("errorExistEmail", "Patient Email ID already exist"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("mode", "add"));

        verify(patientService, never()).addPatient(any(PatientDto.class));  // Service should not be called
    }
    @DisplayName("Junit test for toHandleSetPasswordRequest GET method - positive case")
    @Test
    public void testToHandleSetPasswordRequest() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/addPatient/setPassword")
                .param("savedPatientId", String.valueOf(1L)) );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("patient/addPatient"));
        resultActions.andExpect(model().attributeExists("mode","credentials"
                ,"savedPatientId","setPassword"));
        resultActions.andExpect(model().attribute("savedPatientId",1L));
        resultActions.andExpect(model().attributeDoesNotExist("showPatientForm")); // Ensure showPatientForm fragment disabled
    }
    @DisplayName("Junit test for toHandleSetPasswordRequest GET method - negative case with wrong Url")
    @Test
    public void testToHandleSetPasswordRequest_invalidUrl() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(get("/hospital/addPatient/wrongUrl")
                .param("savedPatientId", String.valueOf(1L)) );
        // Assert
        resultActions.andExpect(status().isNotFound() ); // As given wrongUrl
    }
    @DisplayName("JUnit test for toHandleSetPasswordRequest GET method - negative case with invalid parameter")
    @Test
    public void testToHandleSetPasswordRequest_invalidParameter() throws Exception {
        // Act & Assert: Invalid parameter (non-numeric savedPatientId)
        mockMvc.perform(get("/hospital/addPatient/setPassword")
                        .param("savedPatientId", "invalidId")) // Invalid parameter
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request for invalid parameter

        // Act & Assert: Missing required parameter (savedPatientId)
        mockMvc.perform(get("/hospital/addPatient/setPassword"))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request for missing parameter
    }
    @DisplayName("Junit test for toHandleSetPasswordRequest POST method - Positive case")
    @Test
    public void testToHandleSetPasswordRequest_success() throws Exception {

        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient/setPassword")
                        .param("password","password@123")
                        .param("confirmPassword","password@123")
                        .param("savedPatientId",String.valueOf(1L)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("success",true));
        // verify
        verify(patientService, times(1))
                .setPassword(anyLong(),anyString());
    }

    @DisplayName("JUnit test for toHandleSetPasswordRequest POST method - Negative case with Validation Errors")
    @Test
    public void testToHandleSetPasswordRequest_validationErrors() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient/setPassword")
                        .param("password","password@123")
                        .param("confirmPassword","")  // invalid confirmPassword param
                        .param("savedPatientId",String.valueOf(1L))
               ).andExpect(status().isOk())  // Expecting the form to reload (not redirect)
                .andExpect(view().name("patient/addPatient"))  // Form should be displayed again
                .andExpect(model().attribute("setPassword", "fragmentName"))
                .andExpect(model().attributeExists("mode","savedPatientId"))
                .andExpect(model().attributeDoesNotExist("error", "success"));
        // verify
        verify(patientService, never())
                .setPassword(anyLong(),anyString());
    }
    @DisplayName("JUnit test for toHandleSetPasswordRequest POST method - password mismatch")
    @Test
    public void testToHandleAddPatient_passwordMismatch() throws Exception {

        // Act & Assert
        mockMvc.perform(post("/hospital/addPatient/setPassword")
                        .param("password","password@123")
                        .param("confirmPassword","misMatchPassword")  // mismatching confirmPassword param
                        .param("savedPatientId",String.valueOf(1L))
                ).andExpect(status().isOk())  // Expecting the form to reload (not redirect)
                .andExpect(view().name("patient/addPatient"))  // Form should be displayed again with error message
                .andExpect(model().attribute("setPassword", "fragmentName"))
                .andExpect(model().attributeExists("mode","savedPatientId","error"))
                .andExpect(model().attribute("error","Passwords are not matching"))
                .andExpect(model().attributeDoesNotExist("success"));
        // verify
        verify(patientService, never())
                .setPassword(anyLong(),anyString());
    }
//    toHandlePatientViewProfileRequest
@DisplayName("Junit test for toHandlePatientViewProfileRequest GET method - positive case")
@Test
public void toHandlePatientViewProfileRequest_success() throws Exception {
    // Arrange
    when(patientService.getSinglePatientById(anyLong()))
            .thenReturn(patientDto);  // Mock service method behaviour
    // Act
    ResultActions resultActions = mockMvc.perform(get("/hospital/patientPortal/1") );
    // Assert
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(view().name("patient/patientPortal"));
    resultActions.andExpect(model().attributeExists("mode","patient","showPatientForm"));
}

    @DisplayName("Junit test for toHandlePatientViewProfileRequest GET method - negative case with wrong url")
    @Test
    public void toHandlePatientViewProfileRequest_wrongUrl() throws Exception {
        // Arrange
        when(patientService.getSinglePatientById(anyLong()))
                .thenReturn(patientDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/wrongUrl/1") );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandlePatientViewProfileRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toHandlePatientViewProfileRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/invalidPathStringValue") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @Test
    @DisplayName("Test PatientNotFoundException handling for non-existent patient ID")
    public void testGetPatientProfile_PatientNotFound() throws Exception {
        // Arrange
        long nonExistentPatientId = 99L;

        // Mock the patientService to throw PatientNotFoundException for the ID
        when(patientService.getSinglePatientById(nonExistentPatientId))
                .thenThrow(new PatientNotFoundException(
                        "Patient with ID: " + nonExistentPatientId + " does not exist"));

        // Act & Assert
        mockMvc.perform(get("/hospital/patientPortal/{id}", nonExistentPatientId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        PatientNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Patient with ID: 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());
    }

    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - positive case")
    @Test
    public void toHandleUpdateProfileRequest_success() throws Exception {
        // Arrange
        when(patientService.getSinglePatientById(anyLong()))
            .thenReturn(patientDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform( get("/hospital/patientPortal/updateProfile/1") );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("patient/patientPortal"));
        resultActions.andExpect(model().attributeExists("mode","patient","showPatientForm"));
    }

    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - negative case with wrong url")
    @Test
    public void toHandleUpdateProfileRequest_wrongUrl() throws Exception {
        // Arrange
        when(patientService.getSinglePatientById(anyLong()))
                .thenReturn(patientDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform( get("/hospital/patientPortal/wrongUrl/1") );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toHandleUpdateProfileRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform( get(
                "/hospital/patientPortal/updateProfile/invalidPathVariableType") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @DisplayName("Junit test for toHandleUpdateProfileRequest GET method - negative case with nonExistPatientId")
    @Test
    public void toHandleUpdateProfileRequest_notExistPatientId() throws Exception {
        // Arrange
        long nonExistentPatientId = 99L;
        // Mock the patientService to throw PatientNotFoundException for the ID
        when(patientService.getSinglePatientById(nonExistentPatientId))
                .thenThrow(new PatientNotFoundException(
                        "Patient with ID: " + nonExistentPatientId + " does not exist"));
        // Act & Assert
        mockMvc.perform(get("/hospital/patientPortal/updateProfile/{id}", nonExistentPatientId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        PatientNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Patient with ID: 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());
    }

    @DisplayName("JUnit test for toHandleUpdateProfile PUT method - Positive Case")
    @Test
    public void testToHandleUpdateProfile_Success() throws Exception {
        // Arrange
        Long patientId = 1L;
        patientDto.setEmail("alexbellupdated@gmail.com");

        when( patientService.updatePatientById(anyLong(),any(PatientDto.class))).thenReturn(patientDto);

        // Act & Assert
        mockMvc.perform(put("/hospital/patientPortal/updateProfile/{id}", patientId)
                        .params(convertToParams(patientDto))
                )
//                .andDo(print())// Simulate form submission
                .andExpect(status().is3xxRedirection())  // Check for redirection
                .andExpect(redirectedUrl("/hospital/patientPortal/" + patientId))  // Check correct redirection URL
                .andExpect(flash().attribute("updatedPatient"
                        , hasProperty("email", is("alexbellupdated@gmail.com"))));  // Verify the updated email

        // Verify that the service was called
        verify(patientService, times(1))
                .updatePatientById(eq(patientId), any(PatientDto.class));
    }
    @DisplayName("JUnit test for toHandleUpdateProfile PUT method - Negative Case with Validation Errors")
    @Test
    public void testToHandleUpdateProfile_withValidationErrors() throws Exception {
        // Arrange
        Long patientId = 1L;

        // Act & Assert
        mockMvc.perform(put("/hospital/patientPortal/updateProfile/{id}", patientId)
                        .param("firstName", "")  // Invalid input (empty first name)
                        .param("lastName", "GrahamBell")
                        .param("email", "alexbell@gmail.com")
                )
                .andExpect(status().isOk())  // The page is rendered with validation errors (not a redirect)
                .andExpect(view().name("patient/patientPortal"))  // Renders the same form
                .andExpect(model().attribute("mode", "update"))
                .andExpect(model().hasErrors() )// Ensures the 'model' has errors
                .andExpect(model().attributeExists("showPatientForm"));  // Ensure the form is displayed again

        // Verify the service was not called because of validation errors
        verify(patientService, never()).updatePatientById(anyLong(), any(PatientDto.class));
    }
    @DisplayName("JUnit test for toHandleUpdateProfile PUT method - Negative Case with Non-existent ID")
    @Test
    public void testToHandleUpdateProfile_nonExistentId() throws Exception {
        // Arrange
        when(patientService.updatePatientById(anyLong(), any(PatientDto.class)))
                .thenReturn(null);

        // Act & Assert
        mockMvc.perform(put("/hospital/patientPortal/updateProfile/{id}", 1)
                        .params(convertToParams(patientDto))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hospital/patientPortal/" + 1))  // Check correct redirection URL
                .andExpect(flash().attributeExists("error"))  // Ensure the error flash attribute exists
                .andExpect(flash().attribute("error", "Patient not found with ID: 1"));  // Check the error message


        // Verify service interaction
        verify(patientService, times(1))
                .updatePatientById(anyLong(), any(PatientDto.class));


    }
    @DisplayName("Junit test for toHandleDeletePatientRequest GET method - positive case")
    @Test
    public void toHandleDeletePatientRequest_success() throws Exception {
        // Arrange
        when(patientService.getSinglePatientById(anyLong()))
                .thenReturn(patientDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/deletePatient/1") );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("patient/patientPortal"));
        resultActions.andExpect(model().attributeExists("mode","patient","deletePatient"));
    }
    @DisplayName("Junit test for toHandleDeletePatientRequest GET method - negative case with wrong url")
    @Test
    public void toHandleDeletePatientRequest_wrongUrl() throws Exception {
        // Arrange
        when(patientService.getSinglePatientById(anyLong()))
                .thenReturn(patientDto);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform( get("/hospital/patientPortal/wrongUrl/1") );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandleDeletePatientRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toHandleDeletePatientRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/deletePatient/invalidPathStringValue") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @DisplayName("Junit test for toHandleDeletePatientRequest GET method - negative case with nonExistPatientId")
    @Test
    public void toHandleDeletePatientRequest_notExistPatientId() throws Exception {
        // Arrange
        long nonExistentPatientId = 99L;
        // Mock the patientService to throw PatientNotFoundException for the ID
        when(patientService.getSinglePatientById(nonExistentPatientId))
                .thenThrow(new PatientNotFoundException(
                        "Patient with ID: " + nonExistentPatientId + " does not exist"));
        // Act & Assert
        mockMvc.perform(get("/hospital/patientPortal/deletePatient/{id}", nonExistentPatientId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        PatientNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Patient with ID: 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());

    }
    @DisplayName("JUnit test for toHandleDeletePatient DELETE method - Positive Case")
    @Test
    public void toHandleDeletePatient_Success() throws Exception {
        // Arrange
        Long patientId = 1L;

        // Act & Assert
        mockMvc.perform(delete("/hospital/patientPortal/deletePatient/{id}", patientId) )
//                .andDo(print())// Simulate form submission
                .andExpect(status().is3xxRedirection())  // Check for redirection
                .andExpect(redirectedUrl("/hospital"));

        // Verify that the service was called
        verify(patientService, times(1))
                .deletePatientById(eq(patientId));
    }
    @DisplayName("Junit test for toHandleDeletePatient DELETE method - negative case with wrong url")
    @Test
    public void toHandleDeletePatient_wrongUrl() throws Exception {
                // Act
        ResultActions resultActions = mockMvc.perform( delete("/hospital/patientPortal/wrongUrl/{id}",1) );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toHandleDeletePatient DELETE method - negative case with nonExistPatientId")
    @Test
    public void toHandleDeletePatient_notExistPatientId() throws Exception {
        // Arrange
        Long inValidPatientId = 99L;
        // Act
        ResultActions resultActions = mockMvc.perform(
                delete("/hospital/patientPortal/deletePatient/{id}"
                        ,inValidPatientId) );
        // Assert
        resultActions.andExpect(status().is3xxRedirection());
        resultActions.andExpect(redirectedUrl("/hospital"));

        // Verify that the service was called
        verify(patientService, times(1))
                .deletePatientById(eq(inValidPatientId));
    }
    @DisplayName("JUnit test for toShowDoctorsBySpecialization - Positive Case")
    @Test
    public void testToShowDoctorsBySpecialization_Success() throws Exception {
        // Arrange
        Long patientId = 1L;

        // Mock doctor list
        List<DoctorDto> mockDoctorList = new ArrayList<>();
        DoctorDto doctorDto1 = DoctorDto.builder()
                .id("1")
                .firstName("Dr. Ajaya")
                .lastName("Nand Jha")
                .gender("Male")
                .email("doctor1@gmail.com")
                .specialization("Neurosurgery")
                .qualification("MBBS,MS,FRCS")
                .experience("42")
                .phone("9876543210")
                .image("imageBytes".getBytes())
                .build();

        mockDoctorList.add(doctorDto1);

        when(doctorService.getAllDoctors()).thenReturn(mockDoctorList);
        when(patientService.getSinglePatientById(patientId)).thenReturn(patientDto);

        // Act & Assert
        mockMvc.perform(get("/hospital/patientPortal/{id}/showDoctorsBySpecialization", patientId))
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patientPortal"))
                .andExpect(model().attributeExists("doctorsData"))
                .andExpect(model().attribute("mode", "showDoctorsBySpecialization"))
                .andExpect(model().attribute("showDoctors", "fragmentName"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("doctorsData", hasSize(1)))  // Check that doctorsData contains 1 entry
                // Check the first map inside the doctorsData list
                .andExpect(model().attribute("doctorsData", hasItem(
                        allOf(
                                hasEntry("doctor", doctorDto1)
//                              ,hasEntry(equalTo("base64Image"), equalTo(Base64.getEncoder().encodeToString(doctorDto.getImage())))

                        )
                )));

        verify(doctorService, times(1)).getAllDoctors();
        verify(patientService, times(1)).getSinglePatientById(patientId);
    }
    @DisplayName("JUnit test for toShowDoctorsBySpecialization - Patient Not Found Case")
    @Test
    public void testToShowDoctorsBySpecialization_PatientNotFound() throws Exception {
        // Arrange
        long nonExistentPatientId = 99L;
        // Mock an empty doctor list
        when(doctorService.getAllDoctors()).thenReturn(Collections.emptyList());
        // Mock the patientService to throw PatientNotFoundException for the ID
        when(patientService.getSinglePatientById(nonExistentPatientId))
                .thenThrow(new PatientNotFoundException(
                        "Patient with ID: " + nonExistentPatientId + " does not exist"));
        // Act & Assert
        mockMvc.perform(get("/hospital/patientPortal/{id}/showDoctorsBySpecialization", nonExistentPatientId))
                .andExpect(status().isNotFound()) // Expect 404 status
                .andExpect(result -> assertInstanceOf(
                        PatientNotFoundException.class, result.getResolvedException()))
                .andExpect( result -> assertEquals(
                        "Patient with ID: 99 does not exist",
                        result.getResolvedException().getMessage()) )
                .andDo(print());
        verify(doctorService, times(1)).getAllDoctors();
        verify(patientService, times(1)).getSinglePatientById(nonExistentPatientId);
    }

}


