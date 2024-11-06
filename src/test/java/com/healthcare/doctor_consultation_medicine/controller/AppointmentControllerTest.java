package com.healthcare.doctor_consultation_medicine.controller;

import com.healthcare.doctor_consultation_medicine.Controller.AppointmentController;
import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
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
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
public class AppointmentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    PatientService patientService;
    @MockBean
    DoctorService doctorService;
    @MockBean
    AppointmentService appointmentService;
    @MockBean
    MedicineService medicineService;
    Patient patient,patient2;
    PatientDto patientDto,patientDto2;
    Doctor doctor;
    DoctorDto doctorDto;
    Appointment appointment,appointment2;
    List<Appointment> appointmentList;
    @InjectMocks
    AppointmentController appointmentController;
    @BeforeEach
    public void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(
                 new AppointmentController(appointmentService,doctorService,patientService,medicineService))
                .build();

        patient = Patient.builder()
                .id(1L)
                .firstName("Ratna")
                .lastName("Vel")
                .email("patient1@gmail.com")
                .build();
        patientDto = PatientDto.builder() // secondPatient
                .id("1")
                .firstName("Ratna")
                .lastName("Vel")
                .email("patient1@gmail.com")
                .build();

        patient2 = Patient.builder()
                .id(2L)
                .firstName("Vijay")
                .lastName("Arun")
                .email("patient2@gmail.com")
                .build();
        patientDto2 = PatientDto.builder()
                .id("2")
                .firstName("Vijay")
                .lastName("Arun")
                .email("patient2@gmail.com")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Dr. Ajaya")
                .lastName("Nand Jha")
                .email("doctor1@gmail.com")
                .build();
        doctorDto = DoctorDto.builder()
                .id("1")
                .firstName("Dr. Ajaya")
                .lastName("Nand Jha")
                .email("doctor1@gmail.com")
                .build();

        appointment = Appointment.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2024,10,27))
                .appointmentTime(LocalTime.of(10,30))
                .patient(patient)
                .doctor(doctor)
                .build();
        appointment2 = Appointment.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2024,10,27))
                .appointmentTime(LocalTime.of(11,30)) // At different time but same date
                .patient(patient2)
                .doctor(doctor)
                .build();

        appointmentList= List.of(appointment,appointment2);
    }

    @DisplayName("Junit Test for showFormForAvailableAppointmentSlots GET method - positive case")
    @Test
    public void toTestShowFormForAvailableAppointmentSlots_success() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDate date = LocalDate.of(2024,10,27);

        //Mock service method call behaviour
        when(appointmentService.collectBookedSlots(doctorId,date))
                .thenReturn(appointmentList);
        when(doctorService.getSingleDoctorById(doctorId))
                .thenReturn(doctorDto);
        when(patientService.getSinglePatientById(patientId))
                .thenReturn(patientDto);

        //Act & Assert
        mockMvc.perform(get("/appointment/schedule/{doctorId}/{patientId}",doctorId,patientId)
                        .param("date", String.valueOf(date))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patientPortal"))
                .andExpect(model().attributeExists("appointments","doctor","patient",
                        "date","morningSlots","eveningSlots","scheduleAppointment"));
    }
    @DisplayName("JUnit Test for showFormForAvailableAppointmentSlots - Negative case (Doctor Not Found)")
    @Test
    public void toTestShowFormForAvailableAppointmentSlots_DoctorNotFound() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 27);

        // Mock the behavior where doctorService returns null for a non-existent doctor ID
        when(doctorService.getSingleDoctorById(doctorId)).thenReturn(null); // Simulate doctor not found

        // Act & Assert - Expect a redirect to an error page or a 404 Not Found status
        mockMvc.perform(get("/appointment/schedule/{doctorId}/{patientId}", doctorId, patientId)
                        .param("date", String.valueOf(date)))
                .andExpect(status().isNotFound())  // Check if response status is 404
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class
                        , result.getResolvedException()))
                .andExpect(result -> assertEquals("Doctor not found",
                              ( (ResponseStatusException) result.getResolvedException() ).getReason()
                                                 )
                          );
//                .andDo(print());

        // Verify that the doctorService was called once
        verify(doctorService, times(1)).getSingleDoctorById(doctorId);
        verify(appointmentService,never()).collectBookedSlots(doctorId, date);
        verify(patientService, times(0)).getSinglePatientById(patientId);
    }

    @DisplayName("JUnit Test for showFormForAvailableAppointmentSlots - Negative case (No Patient found)")
    @Test
    public void toTestShowFormForAvailableAppointmentSlots_NoPatientFound() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 27);

        // Mock the behavior where appointment list is empty
        when(doctorService.getSingleDoctorById(doctorId)).thenReturn(doctorDto);
        when(appointmentService.collectBookedSlots(doctorId, date)).thenReturn( appointmentList ); // Empty Appointment List
        when(patientService.getSinglePatientById(doctorId)).thenReturn(null);
        // Act & Assert - Expect a redirect to an error page or a 404 Not Found status
        mockMvc.perform(get("/appointment/schedule/{doctorId}/{patientId}", doctorId, patientId)
                        .param("date", String.valueOf(date)))
                .andExpect(status().isNotFound())  // Check if response status is 404
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class
                        , result.getResolvedException()))
                .andExpect(result -> assertEquals("Patient not found",
                                ( (ResponseStatusException) result.getResolvedException() ).getReason()
                        )
                );
//                .andDo(print());

        // Verify that the doctorService was called once
        verify(doctorService, times(1)).getSingleDoctorById(doctorId);
        verify(appointmentService,times(1)).collectBookedSlots(doctorId, date);
        verify(patientService,times(1)).getSinglePatientById(patientId);
    }
    @DisplayName("JUnit test for bookAppointment - Success case")
    @Test
    public void testBookAppointment_Success() throws Exception {
        // Arrange
        Long doctorId = 1L;
        Long patientId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 27);
        LocalTime time = LocalTime.of(11,30);

        // Mock the service method calls
        when(appointmentService.bookAppointment(anyLong(),anyLong(),any(LocalDate.class),any(LocalTime.class)))
                .thenReturn(appointment);
        // Act & Assert
        mockMvc.perform(get("/appointment/book")
                        .param("doctorId", String.valueOf(doctorId))
                        .param("patientId",String.valueOf(patientId))
                        .param("date",String.valueOf(date))
                        .param("time",String.valueOf(time))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("patient/patientPortal"))
                .andExpect(model().attributeExists("appointment","patient","scheduleAppointmentConfirmation"))
                .andDo(print());

        // Verify that the service was called with the correct arguments
        verify(appointmentService, times(1)).bookAppointment(doctorId,patientId,date,time);
    }
    @DisplayName("JUnit Test for bookAppointment - Negative case (Appointment Not Found)")
    @Test
    public void toTestBookAppointment_AppointmentNotFound() throws Exception {
        // Arrange
        Long doctorId = 99L; // NonExist Doctor Id
        Long patientId = 99L; // NonExist Patient Id
        LocalDate date = LocalDate.of(2024, 10, 27);
        LocalTime time = LocalTime.of(11,30);

        // Mock the behavior where doctorService returns null for a non-existent doctor ID & PatientId
        when(appointmentService.bookAppointment(anyLong(),anyLong(),any(LocalDate.class),any(LocalTime.class)))
                .thenReturn(null);
        // Act & Assert - Expect a 404 Not Found status
        mockMvc.perform(get("/appointment/book")
                        .param("doctorId", String.valueOf(doctorId))
                        .param("patientId",String.valueOf(patientId))
                        .param("date",String.valueOf(date))
                        .param("time",String.valueOf(time))
                )
                .andExpect(status().isNotFound())  // Check if response status is 404
                .andExpect( result -> assertInstanceOf( ResponseStatusException.class
                        , result.getResolvedException() ) )
                .andExpect(result -> {
                            assertEquals("Appointment not found",
                                    ((ResponseStatusException) result.getResolvedException()).getReason()
                            );
                        }
                );
//                .andDo(print());

        // Verify that the service was called with the correct arguments
        verify(appointmentService, times(1)).bookAppointment(doctorId,patientId,date,time);
        verify(patientService, times(0)).getSinglePatientById(patientId);
    }
    @DisplayName("JUnit test for showFormForBookedAppointmentSlotsToDoctor - Success case")
    @Test
    public void testShowFormForBookedAppointmentSlotsToDoctor_Success() throws Exception {
        // Arrange
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 27);

        // Mock the service method calls
        when(doctorService.isExistById(anyLong())).thenReturn(true);
        when(appointmentService.collectBookedSlots(anyLong(),any(LocalDate.class)))
                .thenReturn(appointmentList);
        when(doctorService.getSingleDoctorById(anyLong())).thenReturn(doctorDto);
        // Act & Assert
        mockMvc.perform(get("/appointment/scheduled/{doctorId}",doctorId)
                        .param("date",String.valueOf(date))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctorPortal"))
                .andExpect(model().attributeExists("doctor","date",
                        "morningSlots","eveningSlots","appointments","showBookedAppointments"
                ));
//                .andDo(print());

        // Verify that the service was called with the correct arguments
        verify(doctorService,times(1)).isExistById(doctorId);
        verify(appointmentService, times(1)).collectBookedSlots(doctorId,date);
        verify(doctorService, times(1)).getSingleDoctorById(doctorId);
    }

    @DisplayName("JUnit test for showFormForBookedAppointmentSlotsToDoctor - negative case (Doctor Id not exist)")
    @Test
    public void testShowFormForBookedAppointmentSlotsToDoctor_DoctorNotFound() throws Exception {
        // Arrange
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 27);

        // Mock the service method calls
        when(doctorService.isExistById(anyLong())).thenReturn(false);
        // Act & Assert
        mockMvc.perform(get("/appointment/scheduled/{doctorId}",doctorId)
                        .param("date",String.valueOf(date))
                )
                .andExpect(status().isNotFound())
                .andExpect( result -> assertInstanceOf( ResponseStatusException.class
                        , result.getResolvedException() ) );
//                .andDo(print());

        // Verify that the service was called with the correct arguments
        // Verify that the service was called with the correct arguments
        verify(doctorService,times(1)).isExistById(doctorId);
        verify(appointmentService, times(0)).collectBookedSlots(doctorId,date);
        verify(doctorService, times(0)).getSingleDoctorById(doctorId);
    }

    @DisplayName("JUnit test for showConsultationRoomToDoctorForBookedAppointmentSlots - Success case")
    @Test
    public void testShowConsultationRoomToDoctorForBookedAppointmentSlots_Success() throws Exception {
        // Arrange
        Long doctorId = 1L;
        LocalDate date = LocalDate.of(2024, 10, 27);
        LocalTime time = LocalTime.of(11,30);

        MedicineDto medicineDto1 = MedicineDto.builder()
                .id("1")
                .name("Paracetamol")
                .dosage("10 ml")
                .duration("Night")
                .build();
        MedicineDto medicineDto2 = MedicineDto.builder()
                .id("2")
                .name("Amoxicillin")
                .dosage("100 mg")
                .duration("Morning")
                .build();
        List<MedicineDto> medicineDtoList = List.of(medicineDto1,medicineDto2);

        appointment.setDoctorAdvice("Take the prescribed medication for 3 days, Take Paracetamol 10ml for every 2 hours if fever 103 degree above." +
                " Revisit me after 3 days if fever is not reduced");
        // Mock the service method calls
        when(appointmentService.fetchPatientRecord(anyLong(),any(LocalDate.class),any(LocalTime.class)))
                .thenReturn(patientDto);
        when(appointmentService.fetchAppointment(anyLong(),any(LocalDate.class),any(LocalTime.class)))
                .thenReturn(appointment);
        when(doctorService.getSingleDoctorById(anyLong())).thenReturn(doctorDto);
        when(medicineService.getAllMedicineByPatientId(anyLong())).thenReturn(medicineDtoList);
        // Act & Assert
        mockMvc.perform(get("/appointment/goToConsultationRoom")
                        .param("date",String.valueOf(date))
                        .param("doctorId",String.valueOf(doctorId))
                        .param("time",String.valueOf(time))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctorPortal"))
                .andDo(print())
                .andExpect(model().attributeExists("mode","patient","doctor","doctorId","medicine",
                        "medicineList","appointmentId","advice","showPatientByAppointmentTime"
                ))
                .andExpect(model().attributeDoesNotExist("error"));
//                .andDo(print());

        // Verify that the service was called with the correct arguments
        verify(appointmentService, times(1)).fetchPatientRecord(doctorId,date,time);
        verify(appointmentService, times(1)).fetchAppointment(doctorId,date,time);
        verify(doctorService,times(1)).getSingleDoctorById(doctorId);
        verify(medicineService, times(1)).getAllMedicineByPatientId(doctorId);
    }

    @DisplayName("JUnit test for saveDoctorAdviceOnly - Success case")
    @Test
    public void testSaveDoctorAdviceOnly() throws Exception {
        // Arrange
        Long appointmentId = 1L;

        String doctorAdvice = "Take the prescribed medication for 3 days, Take Paracetamol 10ml for every 2 hours if fever 103 degree above." +
                " Revisit me after 3 days if fever is not reduced";

        MedicineDto medicineDto1 = MedicineDto.builder()
                .id("1")
                .name("Paracetamol")
                .dosage("10 ml")
                .duration("Night")
                .build();
        MedicineDto medicineDto2 = MedicineDto.builder()
                .id("2")
                .name("Amoxicillin")
                .dosage("100 mg")
                .duration("Morning")
                .build();
        List<MedicineDto> medicineDtoList = List.of(medicineDto1,medicineDto2);

        // Mock the service method calls
        when(appointmentService.findById(anyLong())).thenReturn(appointment);
        when(medicineService.getAllMedicineByPatientId(anyLong()))
                .thenReturn(medicineDtoList);

        // Act & Assert
        mockMvc.perform(post("/appointment/saveAdviceOnly")
                        .param("advice",doctorAdvice)
                        .param("appointmentId", String.valueOf(appointmentId))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("doctor/doctorPortal"))
                .andExpect(model().attributeExists("patient","doctor","doctorId","advice",
                        "appointmentId","medicineList","medicine","mode","showPatientByAppointmentTime"
                ));
//                .andDo(print());

        // Verify that the service was called with the correct arguments
        verify(appointmentService, times(1)).findById(appointmentId);
        verify(appointmentService, times(1)).addAdviseToAppointment(appointment);
        verify(medicineService, times(1)).getAllMedicineByPatientId(appointment.getPatient().getId());
    }

}
