package com.healthcare.doctor_consultation_medicine.service;

import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Mapper.PatientMapper;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.Implementation.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

//@MockitoSettings(strictness = Strictness.LENIENT)
public class PatientServiceTest {
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    private Patient patient;
    private PatientDto patientDto;
    @InjectMocks
    PatientServiceImpl patientService;

    @BeforeEach
    public void setUp(){
        patientDto = PatientDto.builder()
                .id("5L")
                .firstName("Alexander")
                .lastName("GrahamBell")
                .age("23")
                .gender("Male")
                .email("alexbell@gmail.com")
                .phone("9876543210")
                .height("172.35F")
                .weight("80.5F")
                .city("New York")
                .address("Door No -10,2nd Street,Manhattan,New York,America")
                .medicalHistory("No Diabetics, No Hyperthermia, Undergone byPass heart surgery 2 years ago")
                .emergencyContactRelationship("Son")
                .emergencyContactNumber("9876543210")
                .build();

        patient = Patient.builder()
                .id(5L)
                .firstName("Alexander")
                .lastName("GrahamBell")
                .age(23)
                .gender("Male")
                .email("alexbell@gmail.com")
                .phone(9876543210L)
                .height(172.35F)
                .weight(80.5F)
                .city("New York")
                .address("Door No -10,2nd Street,Manhattan,New York,America")
                .medicalHistory("No Diabetics, No Hyperthermia, Undergone byPass heart surgery 2 years ago")
                .emergencyContactRelationship("Son")
                .emergencyContactNumber(9876543210L)
                .build();
    }
    @DisplayName("Junit test for testExistByEmailId method - positive case")
    @Test
    public void testExistByEmailId(){
        //Arrange
        when(patientRepository.findOneByEmail("alexbell@gmail.com")).thenReturn(Optional.of(patient));

        //Act
        boolean isPatientExist = patientService.existByEmailId("alexbell@gmail.com");

        //Assert
        assertThat(isPatientExist).isTrue();
    }
    @DisplayName("Junit test for testExistByEmailId method - Negative case")
    @Test
    public void testExistByEmailId_NotFound(){
        //Arrange
        when( patientRepository.findOneByEmail(anyString()) ).thenReturn( Optional.empty() );

        //Act
        boolean isPatientExist = patientService.existByEmailId("nonexistemail@gmail.com");

        //Assert
        assertThat(isPatientExist).isFalse();
    }

    @DisplayName("Junit test for addPatient method - positive case")
    @Test
    public void testAddPatient(){
        // Arrange
        // Mock static methods in PatientMapper using mockStatic()
        try (  MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class) ){
            //set up static method behavior
            mapperMockedStatic.when( () -> PatientMapper.toMapPatientEntity(any(PatientDto.class)))
                    .thenReturn(patient);
            when(patientRepository.save(patient)).thenReturn(patient);

            mapperMockedStatic.when( () -> PatientMapper.toMapPatientDto(any(Patient.class))).thenReturn(patientDto);

            //Act
            PatientDto savedPatientDto = patientService.addPatient(patientDto);

            //Assert
            assertThat(savedPatientDto).isNotNull();
            assertThat(savedPatientDto.getEmail()).isEqualTo(patientDto.getEmail());

            // Verify that the static method was called with the correct argument
            mapperMockedStatic.verify(() -> PatientMapper.toMapPatientEntity(patientDto));
            // Verify that the correct methods were called
            verify(patientRepository, times(1)).save(patient);
            mapperMockedStatic.verify( () -> PatientMapper.toMapPatientDto(patient));

        }

    }

    @DisplayName("Junit test for testAddPatient_dataBaseException method - Negative case")
    @Test
    public void testAddPatient_dataBaseException() {
        try (MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)) {
            //Arrange
            // Now try to addAdviseToAppointment an invalid patient (null firstName, null lastName and duplicate email)
            Patient invalidPatient = Patient.builder()
                    .firstName(null)
                    .lastName(null)
                    .email("alexbell@gmail.com")
                    .build();
            PatientDto invalidPatientDto = PatientDto.builder()
                    .firstName(null)
                    .lastName(null)
                    .email("alexbell@gmail.com")
                    .build();

            mapperMockedStatic.when(() -> PatientMapper.toMapPatientEntity(invalidPatientDto))
                    .thenReturn(invalidPatient);
            // Simulate repository throwing an exception when trying to addAdviseToAppointment invalid patient
            when(patientRepository.save(invalidPatient))
                    .thenThrow(new DataIntegrityViolationException("Null and Unique constraint violation"));
            mapperMockedStatic.when(() -> PatientMapper.toMapPatientDto(invalidPatient))
                    .thenReturn(invalidPatientDto);

            // Act & Assert
            assertThrows(DataIntegrityViolationException.class, () -> {
                   PatientDto savedPatientSecond = patientService.addPatient(
                           invalidPatientDto);
            });

            // Verify addAdviseToAppointment was attempted
            mapperMockedStatic.verify(() -> PatientMapper.toMapPatientEntity(any(PatientDto.class))
                    ,times(1));
            verify(patientRepository, times(1)).save(any(Patient.class));
            mapperMockedStatic.verify( () -> PatientMapper.toMapPatientDto(any(Patient.class)),never());
        }
    }
    @DisplayName("Junit test for testSetPassword method - positive case")
    @Test
    void testSetPassword() {
        // Arrange: Mock repository behavior to return a patient for a valid patientId
        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient)); //Default It is Null
        when(patientRepository.save(patient)).thenReturn(patient);

        // Act
        patientService.setPassword(5L, "newPassword"); // Reset with newPassword

        // Assert
        assertThat(patient.getPassword()).isEqualTo("newPassword");
        verify(patientRepository, times(1)).save(patient);
    }
    @DisplayName("Junit test for testSetPassword method - negative case")
    @Test
    void testSetPassword_inValidPatientId() {
        // Arrange:
        when(patientRepository.findById(5L)).thenReturn(Optional.empty());

        // Act
        patientService.setPassword(5L, "newPassword"); // Reset with newPassword

        // Assert
        assertThat(patient.getPassword()).isNull();
        verify(patientRepository, never()).save(any(Patient.class));
    }
    @DisplayName("Junit test for getSinglePatientById method - positive case")
    @Test
    void getSinglePatientById(){
        when(patientRepository.findById(5L)).thenReturn(Optional.of(patient));

        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){
            mapperMockedStatic.when(() -> PatientMapper.toMapPatientDto(any(Patient.class))).thenReturn(patientDto);

            PatientDto retrievedPatientDto  = patientService.getSinglePatientById(5L);

            assertThat(retrievedPatientDto ).isNotNull();
            assertThat(retrievedPatientDto.getId()).isEqualTo("5L");
            assertThat(retrievedPatientDto.getEmail()).isEqualTo("alexbell@gmail.com");

            verify(patientRepository, times(1)).findById(5L);
        }
    }
    @DisplayName("Junit test for getSinglePatientById method - negative case")
    @Test
    void testGetSinglePatientById_forInvalidPatientId() {
        // Arrange: Mock repository to return Optional.empty() for the given id
        when(patientRepository.findById(200L)).thenReturn(Optional.empty());

        // Act: Call the service method with an invalid patientId
        PatientDto retrievedPatientDto = patientService.getSinglePatientById(200L);

        // Assert: Verify that an empty PatientDto is returned
        assertThat(retrievedPatientDto).isNotNull();
        assertThat(retrievedPatientDto.getId()).isNull(); // since it's a new empty object
        assertThat(retrievedPatientDto.getFirstName()).isNull();
        assertThat(retrievedPatientDto.getLastName()).isNull();

        // Verify repository was called
        verify(patientRepository, times(1)).findById(200L);
    }

    @DisplayName("Junit test for findPatientByEmailId method - positive case")
    @Test
    void testFindPatientByEmailId(){
        when(patientRepository.findOneByEmail("alexbell@gmail.com")).thenReturn(Optional.of(patient));

        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){

            mapperMockedStatic.when(() -> PatientMapper.toMapPatientDto(any(Patient.class)))
                    .thenReturn(patientDto);

            Optional<PatientDto> retrivedPatient = patientService.findPatientByEmailId("alexbell@gmail.com");

            assertThat(retrivedPatient.isPresent()).isTrue();
            retrivedPatient.ifPresent(patientDto1 -> assertThat(patientDto1.getEmail())
                    .isEqualTo("alexbell@gmail.com"));
        }
    }
    @DisplayName("Junit test for findPatientByEmailId method - negative case")
    @Test
    void testFindPatientByEmailId_nonExistEmail(){
        when(patientRepository.findOneByEmail("nonexistpatient@gmail.com"))
                .thenReturn(Optional.empty());

        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){

            mapperMockedStatic.when(() -> PatientMapper.toMapPatientDto(any(Patient.class)))
                    .thenReturn(new PatientDto());

            Optional<PatientDto> retrivedPatient = patientService.findPatientByEmailId("nonexistpatient@gmail.com");

            assertThat(retrivedPatient.isPresent()).isFalse();
        }
    }
    @DisplayName("Junit test for updatePatientById method - positive case")
    @Test
    void testUpdatePatientById(){
        when(patientRepository.existsById(5L)).thenReturn(true);
        try(MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)){
            mapperMockedStatic.when(() -> PatientMapper.toMapPatientEntity(any(PatientDto.class)))
                    .thenReturn(patient);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        patientService.updatePatientById(5L,patientDto);

        // Assert: Verify the addAdviseToAppointment and mapper calls
        verify(patientRepository, times(1)).existsById(5L);
        verify(patientRepository, times(1)).save(patient);
        mapperMockedStatic.verify(() -> PatientMapper.toMapPatientEntity(patientDto), times(1));

        }
    }

    @DisplayName("JUnit test for updatePatientById method - negative case")
    @Test
    void testUpdatePatientById_whenIdDoesNotExist() {
        // Arrange
        when(patientRepository.existsById(200L)).thenReturn(false);

        // Act
        patientService.updatePatientById(200L, patientDto);

        // Assert: Verify that addAdviseToAppointment was NOT called and PatientMapper was not invoked
        verify(patientRepository, times(1)).existsById(200L);
        try (MockedStatic<PatientMapper> mapperMockedStatic = mockStatic(PatientMapper.class)) {
            mapperMockedStatic.verify(() -> PatientMapper.toMapPatientEntity(any(PatientDto.class)), never());
        }
        verify(patientRepository, never()).save(any(Patient.class));
    }
    // If patient exists, all delete operations should be called
    @DisplayName("JUnit test for deletePatientById - positive case")
    @Test
    void testDeletePatientById_whenIdExists() {
        // Arrange
        Long patientId = 5L;
        when(patientRepository.existsById(patientId)).thenReturn(true);

        // Act
        patientService.deletePatientById(patientId);

        // Assert: Verify that the delete methods are called with the correct patient ID
        verify(appointmentRepository, times(1)).deleteByPatientId(patientId);
        verify(medicineRepository, times(1)).deleteByPatientId(patientId);
        verify(patientRepository, times(1)).deleteById(patientId);
    }
    @DisplayName("JUnit test for deletePatientById - negative case")
    @Test
    void testDeletePatientById_whenIdDoesNotExist() {
        // Arrange
        Long patientId = 5L;
        when(patientRepository.existsById(patientId)).thenReturn(false);

        // Act
        patientService.deletePatientById(patientId);

        // Assert: Verify that no delete operations were performed
        verify(appointmentRepository, never()).deleteByPatientId(anyLong());
        verify(medicineRepository, never()).deleteByPatientId(anyLong());
        verify(patientRepository, never()).deleteById(anyLong());
    }
}
