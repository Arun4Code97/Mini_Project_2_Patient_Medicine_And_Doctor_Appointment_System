package com.healthcare.doctor_consultation_medicine.controller;

import com.healthcare.doctor_consultation_medicine.Controller.MedicineController;
import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;


import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(MedicineController.class)
public class MedicineControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MedicineService medicineService;
    @MockBean
    AppointmentService appointmentService;
    @MockBean
    PatientService patientService;
    @InjectMocks
    MedicineController medicineController;
     MedicineDto  medicineDto;

//    public static MultiValueMap<String, String> convertToParams(MedicineDto tempMedicineDto) {
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("id", tempMedicineDto.getId());
//        params.add("name", tempMedicineDto.getName());
//        params.add("dosage", tempMedicineDto.getDosage());
//        params.add("duration", tempMedicineDto.getDuration());
//        return params;
//    }

    @BeforeEach
    public void setUp(){

        mockMvc = MockMvcBuilders.standaloneSetup(
                new MedicineController(medicineService,appointmentService,patientService)).build();

        medicineDto = MedicineDto.builder()
            .id("1")
            .name("Paracetamol")
            .dosage("650 mg")
            .duration("Morning-Night")
            .build();
    }
    @DisplayName("Junit test for addMedicine - Success case")
    @Test
    public void toTestAddMedicine_SuccessCase() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long patientId = 1L;
        Long appointmentId = 1L;
//        MedicineDto medicineDto = new MedicineDto();  // A sample instance of MedicineDto
        Appointment appointment = Appointment.builder()
                .appointmentDate(LocalDate.of(2024,10,24))
                .appointmentTime(LocalTime.of(11,30))
                .build();
        // Mock the service method
        when(medicineService.addMedicine(anyLong(), anyLong(),eq(medicineDto)))
                .thenReturn(medicineDto);
        when(appointmentService.findById(appointmentId))
                .thenReturn(appointment);
        // Act & Assert
        mockMvc.perform(post("/doctorPortal/{doctorId}/addMedicine/{patientId}/{appointmentId}"
                        , doctorId, patientId, appointmentId)
//                        .params(convertToParams(medicineDto)) // used when passing data like form inputs (name=value).
                          .flashAttr("medicine",medicineDto) // when passing complex objects like DoctorDto or MedicineDto.
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointment/goToConsultationRoom?" +
                        "doctorId=1&date=2024-10-24&time=11:30"));
    }
    @DisplayName("JUnit test for getMedicineById - Success and Not Found cases")
    @Test
    public void testGetMedicineById_Success() throws Exception {
        Long medicineId = 1L;
        when(medicineService.getSingleMedicineById(1L)).thenReturn(medicineDto);

        // Act & Assert - Success Case
        mockMvc.perform( get("/medicine/get/{id}",medicineId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Expect HTTP 200
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))     /*Note - REST API Call*/
                .andExpect(jsonPath("$.id").value(medicineDto.getId()))
                .andExpect(jsonPath("$.name").value(medicineDto.getName()))
                .andExpect( jsonPath("$.dosage").value(medicineDto.getDosage()))
                .andExpect(jsonPath("$.duration").value(medicineDto.getDuration()))
                .andDo(print());
    }
    @DisplayName("JUnit test for getMedicineById - Id Not Found cases")
    @Test
    public void testGetMedicineById_NotFound() throws Exception {
        // Arrange
        Long medicineId = 99L;
        when(medicineService.getSingleMedicineById(medicineId)).thenReturn(null);

        // Act & Assert - Not Found Case
        mockMvc.perform(get("/medicine/get/{id}", medicineId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())  // Expect HTTP 404
                .andDo(print());
    }
    @DisplayName("Junit test for toShowMedicinesRequest GET method - positive case")
    @Test
    public void toShowMedicinesRequest_Success() throws Exception {
        // Arrange
        Long medicineId = 1L;
        when(patientService.getSinglePatientById(anyLong()))
                .thenReturn(new PatientDto());  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/{id}/medicines",medicineId) );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(view().name("patient/patientPortal"));
        resultActions.andExpect(model().attributeExists("mode","medicineView","patient"));
        resultActions.andExpect(model().attribute("patient", notNullValue()));
    }
    @DisplayName("Junit test for toShowMedicinesRequest GET method - negative case with wrong url")
    @Test
    public void toShowMedicinesRequest_wrongUrl() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/{id}/medicinesWrongUrl",1L) );
        // Assert
        resultActions.andExpect(status().isNotFound());
    }
    @DisplayName("Junit test for toShowMedicinesRequest GET method - negative case with inValidPathVariable")
    @Test
    public void toShowMedicinesRequest_invalidPathVariable() throws Exception {
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/{id}/medicines","inValidPathVariableType") );
        // Assert
        resultActions.andExpect(status().isBadRequest());
    }
    @DisplayName("Junit test for toShowMedicinesRequest GET method - negative case with nonExistPatientId")
    @Test
    public void toShowMedicinesRequest_notExistPatientId() throws Exception {
        // Arrange
        Long notExistPatientId = 99L;
        when(patientService.getSinglePatientById(anyLong()))
                .thenReturn(null);  // Mock service method behaviour
        // Act
        ResultActions resultActions = mockMvc.perform(
                get("/hospital/patientPortal/{id}/medicines",notExistPatientId) );
        // Assert
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(model().attribute("patient", nullValue()));
    }
    @DisplayName("JUnit test for updateMedicine - Success case")
    @Test
    public void testUpdateMedicine_Success() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long appointmentId = 1L;
        medicineDto.setDosage("1000 mg");
        MedicineDto updatedMedicine = medicineDto;
        // Set up the mock Appointment
        Appointment appointment = Appointment.builder()
                .appointmentDate(LocalDate.of(2024, 10, 24))
                .appointmentTime(LocalTime.of(11, 30))
                .build();

        // Mock the service method calls
        when(medicineService.updateMedicine(anyLong(),any())).thenReturn(updatedMedicine);
        when(appointmentService.findById(appointmentId)).thenReturn(appointment);
        // Act & Assert
        mockMvc.perform(post("/doctorPortal/{doctorId}/updateMedicine/{appointmentId}", doctorId, appointmentId)
                        .flashAttr("medicine", updatedMedicine))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/appointment/goToConsultationRoom?doctorId=1&date=2024-10-24&time=11:30"))
                .andDo(print());

        // Verify that the service was called with the correct arguments
        verify(medicineService, times(1)).updateMedicine(Long.parseLong(updatedMedicine.getId()), updatedMedicine);
        verify(appointmentService, times(1)).findById(appointmentId);
    }
    @DisplayName("JUnit test for updateMedicine - Negative case (Appointment Not Found)")
    @Test
    public void testUpdateMedicine_AppointmentNotFound() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long appointmentId = 1L;
        medicineDto.setDosage("1000 mg");
        // Set up the MedicineDto to use as input
        MedicineDto updatedMedicine = medicineDto;

        // Mock the service method calls
        when(medicineService.updateMedicine(anyLong(),any())).thenReturn(updatedMedicine);
        when(appointmentService.findById(appointmentId)).thenReturn(null);

        // Act & Assert - Expect 404 due to missing appointment
        mockMvc.perform(post("/doctorPortal/{doctorId}/updateMedicine/{appointmentId}", doctorId, appointmentId)
                        .flashAttr("medicine", updatedMedicine))
                .andExpect(status().isNotFound())  // Expect HTTP 404 for missing appointment
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class
                                    , result.getResolvedException())
                          )  // Assert exception type
                .andDo(print());

        // Verify that updateMedicine was not called because the appointment was not found
        verify(medicineService,times(1)).updateMedicine(anyLong(), eq(updatedMedicine));
        verify(appointmentService, times(1)).findById(appointmentId);
    }
    @Test
    @DisplayName("JUnit test for deleteMedicineById - Success case")
    public void testDeleteMedicineById_Success() throws Exception {
        // Arrange
        Long medicineId = 1L;

        // Act & Assert
        mockMvc.perform(get("/deleteMedicine/{medicineId}", medicineId)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))     /*Note - REST API Call*/
                .andExpect(content().string("deleted"))
                .andDo(print());

        // Verify that deleteMedicineById was called once
        verify(medicineService, times(1)).deleteMedicineById(medicineId);
    }

}
