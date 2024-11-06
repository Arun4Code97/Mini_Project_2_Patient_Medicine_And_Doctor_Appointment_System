package com.healthcare.doctor_consultation_medicine.service;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Mapper.PatientMapper;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.Implementation.AppointmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {
    @Mock
    AppointmentRepository appointmentRepository;
    @Mock
    DoctorRepository doctorRepository;
    @Mock
    PatientRepository patientRepository;

    @InjectMocks
    AppointmentServiceImpl appointmentService;

    Patient patient;
    PatientDto patientDto;
    Doctor  doctor;

    Appointment appointment;

    @BeforeEach()
    public void setUp(){
        patient = Patient.builder()
                .id(1L)
                .firstName("alex")
                .lastName("bell")
                .email("alexbell@gmail.com")
                .build();

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Dr.Harish")
                .lastName("Kumar")
                .email("drharish@gmail.com")
                .build();

       appointment = Appointment.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2024,10,19))
                .appointmentTime(LocalTime.of(11,0))
                .doctor(doctor)
                .patient(patient)
                .build();
    }
    @DisplayName(value = "JUnit test for saveAppointment method")
    @Test
    public void testSaveAppointment(){

        // Act
        appointmentService.addAdviseToAppointment(appointment);
        // Assert
        verify(appointmentRepository, times(1)).save(appointment);
    }
    @DisplayName(value = "JUnit test for saveAppointment with missing time (Negative case)")
    @Test
    public void testSaveAppointment_inValidAppointment() {
        // Arrange
        Appointment invalidAppointment = Appointment.builder()
                .appointmentDate(LocalDate.now())
                .appointmentTime(null)
                .build();
        when(appointmentRepository.save(any(Appointment.class)))
                .thenThrow(new DataIntegrityViolationException("appointment time should not be null"));

        // Act & Assert - Expecting an exception or invalid addAdviseToAppointment attempt
        assertThrows(DataIntegrityViolationException.class, () -> {
            appointmentService.addAdviseToAppointment(invalidAppointment); // This should throw an exception
        });
    }
    @DisplayName("JUnit test for findById - positive case")
    @Test
    public void testFindById_AppointmentFound() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        // Act
        Appointment foundAppointment = appointmentService.findById(1L);

        // Assert
        assertThat(foundAppointment).isNotNull();
        assertThat(foundAppointment.getId()).isEqualTo(1L);
        assertThat(foundAppointment.getAppointmentDate()).
                isEqualTo(LocalDate.of(2024, 10, 19));
        verify(appointmentRepository, times(1)).findById(1L);
    }
    @DisplayName("JUnit test for findById - negative case (Appointment not found)")
    @Test
    public void testFindById_AppointmentNotFound() {
        // Arrange
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Appointment foundAppointment = appointmentService.findById(1L);

        // Assert
        assertThat(foundAppointment).isNull();
        verify(appointmentRepository, times(1)).findById(1L);
    }
    @DisplayName("JUnit test for fetchPatientRecord - positive case")
    @Test
    public void testFetchPatientRecord_whenExist() {
        // Arrange
        patientDto = PatientDto.builder()
                        .id("1")
                        .firstName("Alex")
                        .lastName("bell")
                        .email("alexbell@gmail.com")
                        .build();
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                        ,LocalDate.of(2024,10,19)
                        ,LocalTime.of(11,0)))
                .thenReturn(appointment);
        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){
            mapperMockedStatic.when(() -> PatientMapper.toMapPatientDto(any(Patient.class)))
                    .thenReturn(patientDto);
            // Act
            PatientDto patientDto = appointmentService.fetchPatientRecord(1L
                    ,LocalDate.of(2024,10,19)
                    ,LocalTime.of(11,0));

            // Assert
            assertThat(patientDto).isNotNull();
            assertThat(patientDto.getId()).isEqualTo("1");
            verify(appointmentRepository, times(1))
                    .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                            ,LocalDate.of(2024,10,19)
                            ,LocalTime.of(11,0));
            // Verify
            mapperMockedStatic.verify(() -> PatientMapper.toMapPatientDto(any(Patient.class))
                    ,times(1));

        }

    }
    @DisplayName("JUnit test for fetchPatientRecord - negative case (Appointment not found)")
    @Test
    public void testFetchPatientRecord_whenAppointmentNotFound() {
        // Arrange
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0)))
                .thenReturn(null);  // Simulate no appointment found
        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){
        // Act & Assert
        assertThrows(NullPointerException.class ,() -> {
            PatientDto patientDto = appointmentService.fetchPatientRecord(1L
                    ,LocalDate.of(2024, 10, 19)
                    ,LocalTime.of(11, 0));
            assertThat(patientDto).isNull();
        });

        // Verify
        verify(appointmentRepository, times(1))
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0));

            mapperMockedStatic.verify(() -> PatientMapper.toMapPatientDto(any(Patient.class))
                    ,never());
        }
    }
    @DisplayName("JUnit test for fetchPatientRecord - negative case (Patient data is null)")
    @Test
    public void testFetchPatientRecord_whenPatientDataIsNull() {

        // Arrange
        Appointment appointmentWithNullPatient = Appointment.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2024,10,19))
                .appointmentTime(LocalTime.of(11,0))
                .patient(null) // Simulate appointment with no patient
                .build();
        when( appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0)))
                .thenReturn(appointmentWithNullPatient);  // Return appointment, but with null patient

        // Act
        assertThrows(NullPointerException.class ,() -> {
                    PatientDto patientDto = appointmentService.fetchPatientRecord(1L
                            , LocalDate.of(2024, 10, 19)
                            , LocalTime.of(11, 0));
                });

        // Verify
        verify(appointmentRepository, times(1))
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                        , LocalDate.of(2024, 10, 19)
                        , LocalTime.of(11, 0));
    }

    @DisplayName("JUnit test for fetchPatientRecord - negative case (Invalid doctor ID)")
    @Test
    public void testFetchPatientRecord_whenInvalidDoctorId() {
        // Arrange
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(anyLong()
                        ,eq(LocalDate.of(2024, 10, 19))
                        ,eq(LocalTime.of(11, 0))))
                .thenReturn( null); // Simulate no appointment found due to invalid doctor ID
        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){
        // Act & Assert
        assertThrows(NullPointerException.class ,() -> {
            PatientDto patientDto = appointmentService.fetchPatientRecord(999L  // Invalid doctor ID
                    ,LocalDate.of(2024, 10, 19)
                    ,LocalTime.of(11, 0));
        });
        // Verify
        verify(appointmentRepository, times(1))
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(999L
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0));

        mapperMockedStatic.verify(() -> PatientMapper.toMapPatientDto(any(Patient.class))
                    ,never());
        }
    }
    @DisplayName("JUnit test for fetchAppointment - positive case")
    @Test
    public void testFetchAppointment_whenExist() {
        // Arrange
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                        ,LocalDate.of(2024,10,19)
                        ,LocalTime.of(11,0)))
                .thenReturn(appointment);

        // Act
        Appointment retrievedAppointment = appointmentService.fetchAppointment(1L
                    ,LocalDate.of(2024,10,19)
                    ,LocalTime.of(11,0));

        // Assert
        assertThat(retrievedAppointment).isNotNull();
        assertThat(retrievedAppointment.getDoctor()).isEqualTo(doctor);
        assertThat(retrievedAppointment.getPatient()).isEqualTo(patient);
        verify(appointmentRepository, times(1))
                    .findByDoctorIdAndAppointmentDateAndAppointmentTime(1L
                            ,LocalDate.of(2024,10,19)
                            ,LocalTime.of(11,0));
    }
    @DisplayName("JUnit test for fetchAppointment - negative case (Appointment not found)")
    @Test
    public void testFetchAppointment_whenNotFound() {
        // Arrange
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(99L
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0)))
                .thenReturn(null);  // Simulate no appointment found
       // Act

        Appointment retrivedAppointment = appointmentService.fetchAppointment(99L
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0));
        // Assert
        assertThat(retrivedAppointment).isNull();

        // Verify
        verify(appointmentRepository, times(1))
                    .findByDoctorIdAndAppointmentDateAndAppointmentTime(99L
                            ,LocalDate.of(2024, 10, 19)
                            ,LocalTime.of(11, 0));

    }
    @DisplayName("JUnit test for fetchAppointment - negative case (AppointmentDate & Time is null)")
    @Test
    public void testFetchAppointment_whenNullConstraints() {

        // Arrange
        Appointment appointmentWithNullConstraints = Appointment.builder()
                .id(1L)
                .appointmentDate(null)
                .appointmentTime(null)
                .patient(patient)
                .doctor(doctor)
                .build();
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime( anyLong()
                        ,eq(null)
                        ,eq(null)) )
                .thenReturn( null);

        // Act
            Appointment retrivedAppointment = appointmentService.fetchAppointment(1L
                    , null
                    , null);
        //Assert
        assertThat(retrivedAppointment).isNull();
        // Verify
        verify(appointmentRepository, times(1))
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(eq(1L)
                        , eq(null)
                        , eq(null));
    }

    @DisplayName("JUnit test for fetchAppointment - negative case (Invalid doctor ID)")
    @Test
    public void testFetchAppointment_whenInvalidDoctorId() {
        // Arrange
        when(appointmentRepository
                .findByDoctorIdAndAppointmentDateAndAppointmentTime(anyLong() // Invalid doctor ID
                        ,eq(LocalDate.of(2024, 10, 19))
                        ,eq(LocalTime.of(11, 0))))
                .thenReturn( null); // Simulate no appointment found due to invalid doctor ID

        // Act
        Appointment retrievedAppointment = appointmentService.fetchAppointment(999L  // Invalid doctor ID
                        ,LocalDate.of(2024, 10, 19)
                        ,LocalTime.of(11, 0));
        //Assert
        assertThat(retrievedAppointment).isNull();
        // Verify
        verify(appointmentRepository, times(1))
                    .findByDoctorIdAndAppointmentDateAndAppointmentTime(999L
                            ,LocalDate.of(2024, 10, 19)
                            ,LocalTime.of(11, 0));
    }
    @DisplayName("JUnit test for getAllAppointment - positive case")
    @Test
    void testGetAllAppointment_whenAppointmentsExist() {
        // Arrange: Set up the list of appointments returned by the repository
        // doctorId -1 --> has 2 Appointments with different patients
       Patient patient2 = Patient.builder()
                .id(2L)
                .firstName("Ram")
                .lastName("Kumar")
                .email("ramkumar@gmail.com")
                .build();
       Appointment appointment2 = Appointment.builder()
                .id(2L)
                .appointmentDate(LocalDate.of(2024,10,19))
                .appointmentTime(LocalTime.of(11,30)) // different time
                .doctor(doctor) // with same doctor
                .patient(patient2)
                .build();
        List<Appointment> appointmentList = Arrays.asList(
                appointment ,
                appointment2
        );

        when(appointmentRepository.findByDoctorIdAndAppointmentDate(
                1L,
                LocalDate.of(2024,10,19)
        )).thenReturn(appointmentList);

        // Act
        List<Appointment> retrievedAppointments = appointmentService.collectBookedSlots(
                1L,
                LocalDate.of(2024,10,19));

        // Assert
            assertThat(retrievedAppointments).isNotNull();
            assertThat(retrievedAppointments.size()).isEqualTo(2);
            assertThat(retrievedAppointments.get(0).getPatient().getEmail()).isEqualTo("alexbell@gmail.com");
            assertThat(retrievedAppointments.get(0).getDoctor().getEmail()).isEqualTo("drharish@gmail.com");
            assertThat(retrievedAppointments.get(1).getPatient().getEmail()).isEqualTo("ramkumar@gmail.com");
            assertThat(retrievedAppointments.get(1).getDoctor().getEmail()).isEqualTo("drharish@gmail.com");

        // Verify that findAll() was called exactly once
            verify(appointmentRepository, times(1)).findByDoctorIdAndAppointmentDate(
                    anyLong(),
                    any(LocalDate.class));
        }
    @DisplayName("JUnit test for getAllAppointments - when no appointments exist")
    @Test
    void testGetAllAppointments_whenNoAppointmentExist() {
        // Arrange
        when(appointmentRepository.findByDoctorIdAndAppointmentDate(
                 anyLong()
                ,any(LocalDate.class))
                ).thenReturn(Collections.emptyList()
            );
        // Act
        List<Appointment> appointmentList = appointmentService.collectBookedSlots(
                1L,
                LocalDate.of(2024,10,19)
                );
        // Assert
        assertThat(appointmentList).isNotNull();
        assertThat(appointmentList.isEmpty()).isTrue();

        // Verify
        verify( appointmentRepository, times(1) )
                .findByDoctorIdAndAppointmentDate(
                        anyLong(),
                        any(LocalDate.class)
                );
    }
    @DisplayName("JUnit test for bookAppointment - positive case")
    @Test
    public void testBookAppointment_successfulBooking() {

        // Mock the repository methods
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        // Act
        Appointment savedAppointment = appointmentService
                .bookAppointment(1L
                        ,1L
                        , LocalDate.of(2024, 10, 19)
                        , LocalTime.of(11, 0) );

        // Assert
        assertThat(savedAppointment).isNotNull();
        assertThat(savedAppointment.getDoctor()).isEqualTo(doctor);
        assertThat(savedAppointment.getPatient()).isEqualTo(patient);
        assertThat(savedAppointment.getAppointmentDate())
                .isEqualTo(LocalDate.of(2024, 10, 19));
        assertThat(savedAppointment.getAppointmentTime())
                .isEqualTo(LocalTime.of(11, 0));

        // Verify
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
    @DisplayName("JUnit test for bookAppointment - negative case (Invalid Doctor ID)")
    @Test
    public void testBookAppointment_invalidDoctorId() {
        // Arrange: Simulate doctor not found
        Appointment appointment2 = Appointment.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2024,10,19))
                .appointmentTime(LocalTime.of(11,30))
                .doctor(null)
                .patient(patient)
                .build();

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment2);

        // Act
        Appointment retrivedAppointment = appointmentService.bookAppointment(1L, 1L
                , LocalDate.of(2024, 10, 19)
                , LocalTime.of(11, 0));

        // Assert
        assertThat(retrivedAppointment).isNotNull();
        assertThat(retrivedAppointment.getDoctor()).isNull();

        // Verify interactions
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }
    @DisplayName("JUnit test for bookAppointment - negative case (Invalid Patient ID)")
    @Test
    public void testBookAppointment_invalidPatientId() {
        // Arrange: Simulate patient not found
        Appointment appointment2 = Appointment.builder()
                .id(1L)
                .appointmentDate(LocalDate.of(2024,10,19))
                .appointmentTime(LocalTime.of(11,30))
                .doctor(doctor)
                .patient(null)
                .build();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment2);

        // Act
        Appointment retrivedAppointment = appointmentService.bookAppointment(1L
                    , 1L
                    , LocalDate.of(2024, 10, 19)
                    , LocalTime.of(11, 0));

        // Assert
        assertThat(retrivedAppointment).isNotNull();
        assertThat(retrivedAppointment.getPatient()).isNull();

        // Verify interactions
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class)); // No save should happen
    }

    @DisplayName("JUnit test for bookAppointment - negative case (Null Appointment Date and Time)")
    @Test
    public void testBookAppointment_nullDateAndTime() {
        // Arrange: Set up valid doctor and patient, but null date and time
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class)))
                .thenThrow(new DataIntegrityViolationException("appointment time should not be null"));
        // Act & Assert
        assertThrows(DataIntegrityViolationException.class, () -> {
            appointmentService.bookAppointment(1L, 1L, null, null);
        });

        // Verify interactions
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(appointmentRepository, times(1)).save(any(Appointment.class)); // No save should happen
    }

}
