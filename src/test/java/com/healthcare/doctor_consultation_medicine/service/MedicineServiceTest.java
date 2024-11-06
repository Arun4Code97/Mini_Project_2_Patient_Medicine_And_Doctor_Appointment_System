package com.healthcare.doctor_consultation_medicine.service;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Mapper.MedicineMapper;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import com.healthcare.doctor_consultation_medicine.Service.Implementation.MedicineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MedicineServiceTest {
    @Mock
    private MedicineRepository medicineRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    MedicineDto medicineDto;
    Medicine medicine;
    Patient patient;
    Doctor doctor;
    @InjectMocks
    private MedicineServiceImpl medicineService;
    @BeforeEach
    public void setUp(){
        medicineDto = MedicineDto.builder()
                .id("1")
                .name("Torglip M 50/500")
                .dosage("1 tab")
                .duration("Morning & Night")
                .build();
        medicine = Medicine.builder()
                .id(1L)
                .name("Torglip M 50/500")
                .dosage("1 tab")
                .duration("Morning & Night")
                .build();
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
    }

    @DisplayName("JUnit test for addMedicine Method - positive case")
    @Test
    public void testAddMedicine_whenAllFound(){
        //Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        try(MockedStatic<MedicineMapper> mapperMockedStatic = mockStatic(MedicineMapper.class)){
            mapperMockedStatic.when(() -> MedicineMapper.toMapDto(any(Medicine.class)))
                    .thenReturn(medicineDto);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);
            mapperMockedStatic.when(() -> MedicineMapper.toMapEntity(any(MedicineDto.class)))
                    .thenReturn(medicine);
        // Act
            MedicineDto savedMedicine = medicineService.addMedicine(1L,1L,medicineDto);

        // Assert
            assertThat(savedMedicine).isNotNull();
            assertThat(savedMedicine.getName()).isEqualTo("Torglip M 50/500");
            assertThat(savedMedicine.getDoctor().getEmail()).isEqualTo("drharish@gmail.com");
            assertThat(savedMedicine.getPatient().getEmail()).isEqualTo("alexbell@gmail.com");
        // Verify
            mapperMockedStatic.verify(() -> MedicineMapper.toMapEntity(any(MedicineDto.class))
                    ,times(1));
            verify(medicineRepository,times(1)).save(any(Medicine.class));
            mapperMockedStatic.verify(() -> MedicineMapper.toMapDto(any(Medicine.class))
                    , times(1));
        }
    }
    @DisplayName("JUnit test for addMedicine Method - negative case (Doctor not found)")
    @Test
    public void testAddMedicine_DoctorNotFound() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
//        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient)); --> Commented out here as the above setup throws an Exception

        // Act & Assert
        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            medicineService.addMedicine(1L, 1L, medicineDto);
        });

        // Assert exception message
        assertThat(exception.getMessage()).contains("Doctor id 1 not found");

        // Verify that repository methods were called
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(0)).findById(1L);
        verify(medicineRepository, never()).save(any(Medicine.class));
    }

    @DisplayName("JUnit test for addMedicine Method - negative case (Patient not found)")
    @Test
    public void testAddMedicine_PatientNotFound() {
        // Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            medicineService.addMedicine(1L, 1L, medicineDto);
        });

        // Verify that repository methods were called
        verify(doctorRepository, times(1)).findById(1L);
        verify(patientRepository, times(1)).findById(1L);
        verify(medicineRepository, never()).save(any(Medicine.class));
    }
    @DisplayName("Junit test for testAddMedicine_dataBaseException method - Negative case")
    @Test
    public void testAddMedicine_dataBaseException() {
        try (MockedStatic<MedicineMapper> mapperMockedStatic = mockStatic(MedicineMapper.class)) {
            //Arrange
            // To addAdviseToAppointment an invalid medicine (null name, null duration and null dosage email)

            MedicineDto invalidMedicineDto = MedicineDto.builder()
                    .id(null)
                    .name(null)
                    .dosage(null)
                    .duration(null)
                    .build();
            Medicine invalidMedicine = Medicine.builder()
                    .id(null)
                    .name(null)
                    .dosage(null)
                    .duration(null)
                    .build();

            when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
            when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
            mapperMockedStatic.when(() -> MedicineMapper.toMapEntity(any(MedicineDto.class)))
                    .thenReturn(invalidMedicine);
            // Simulate repository throwing an exception when trying to addAdviseToAppointment invalid Medicine
            when(medicineRepository.save(any(Medicine.class)))
                    .thenThrow(new DataIntegrityViolationException("Null constraint violation"));
            mapperMockedStatic.when(() -> MedicineMapper.toMapDto(any(Medicine.class)))
                    .thenReturn(medicineDto);

            // Act & Assert
            assertThrows(DataIntegrityViolationException.class, () -> {
                medicineService.addMedicine(1L, 1L, invalidMedicineDto);
            });

            // Verify that below methods were called
            verify(doctorRepository, times(1)).findById(1L);
            verify(patientRepository, times(1)).findById(1L);
            verify(medicineRepository, times(1)).save(any(Medicine.class));
            mapperMockedStatic.verify(() -> MedicineMapper.toMapEntity(any(MedicineDto.class))
                    ,times(1));
            mapperMockedStatic.verify(() -> MedicineMapper.toMapDto(any(Medicine.class))
                    , never());
        }
    }
    @DisplayName("Junit test for getSingleMedicineById method - positive case")
    @Test
    void getSingleMedicineById(){
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        try(MockedStatic<MedicineMapper> mapperMockedStatic = mockStatic(MedicineMapper.class)){
            mapperMockedStatic.when(() -> MedicineMapper.toMapDto(any(Medicine.class)))
                    .thenReturn(medicineDto);

            MedicineDto retrievedMedicineDto = medicineService.getSingleMedicineById(1L);

            assertThat(retrievedMedicineDto).isNotNull();
            assertThat(retrievedMedicineDto.getId()).isEqualTo("1");
            assertThat(retrievedMedicineDto.getName()).isEqualTo("Torglip M 50/500");

            verify(medicineRepository, times(1)).findById(1L);
            mapperMockedStatic.verify(() -> MedicineMapper.toMapDto(any(Medicine.class))
                    ,times(1));
        }
    }
    @DisplayName("Junit test for getSingleMedicineById method - negative case")
    @Test
    void testGetSingleDoctorById_forInvalidDoctorId() {
        // Arrange: Mock repository to return Optional.empty() for the given id
        when(medicineRepository.findById(200L)).thenReturn(Optional.empty());

        try(MockedStatic<MedicineMapper> mapperMockedStatic = mockStatic(MedicineMapper.class)){
            mapperMockedStatic.when(() -> MedicineMapper.toMapDto(any(Medicine.class)))
                    .thenReturn(new MedicineDto());
            // Act: Call the service method with an invalid medicineId
            MedicineDto retrievedMedicineDto = medicineService.getSingleMedicineById(200L);

            // Assert: Verify that retrievedMedicineDto is null
            assertThat(retrievedMedicineDto).isNull();

            // Verify repository was called
            verify(medicineRepository, times(1)).findById(200L);
            mapperMockedStatic.verify(() -> MedicineMapper.toMapDto(any(Medicine.class))
                    ,never());
        }
    }

    @DisplayName("JUnit test for getAllMedicinesByPatientId - positive case")
    @Test
    void testGetAllMedicinesByPatientId_whenMedicinesExist() {
        // Arrange: Set up the list of medicines returned by the repository
        Medicine firstMedicine = Medicine.builder()
                .id(1L)
                .name("Lipitas 20")
                .dosage("1 tab")
                .duration("Night")
                .patient(patient)
                .doctor(doctor)
                .build();
        MedicineDto firstMedicineDto = MedicineDto.builder()
                .id("1")
                .name("Lipitas 20")
                .dosage("1 tab")
                .duration("Night")
                .patient(patient)
                .doctor(doctor)
                .build();
        Medicine secondMedicine = Medicine.builder()
                .id(2L)
                .name("Resner 5")
                .dosage("1 tab")
                .duration("Morning")
                .patient(patient)
                .doctor(doctor)
                .build();
        MedicineDto secondMedicineDto = MedicineDto.builder()
                .id("2")
                .name("Resner 5")
                .dosage("1 tab")
                .duration("Morning")
                .patient(patient)
                .doctor(doctor)
                .build();
        List<Medicine> medicineList = Arrays.asList(
               firstMedicine,secondMedicine
        );

        when(medicineRepository.findAllByPatientId(1L)).thenReturn(medicineList);

        // Mock the static method behavior of DoctorMapper
        try (MockedStatic<MedicineMapper> mapperMockedStatic = mockStatic(MedicineMapper.class)) {
            mapperMockedStatic.when(() -> MedicineMapper.toMapDto(medicineList.get(0)))
                    .thenReturn(firstMedicineDto);
            mapperMockedStatic.when(() -> MedicineMapper.toMapDto(medicineList.get(1)))
                    .thenReturn(secondMedicineDto);

            // Act: Call the method
            List<MedicineDto> medicineDtosList = medicineService.getAllMedicineByPatientId(1L);

            // Assert: Verify that the list of DTOs is correct
            assertThat(medicineDtosList).isNotNull();
            assertThat(medicineDtosList.size()).isEqualTo(2);
            assertThat(medicineDtosList.get(0).getId()).isEqualTo("1");
            assertThat(medicineDtosList.get(0).getName()).isEqualTo("Lipitas 20");
            assertThat(medicineDtosList.get(1).getId()).isEqualTo("2");
            assertThat(medicineDtosList.get(1).getName()).isEqualTo("Resner 5");

            // Verify that findAll() was called exactly once
            verify(medicineRepository, times(1)).findAllByPatientId(anyLong());
        }
    }
    @DisplayName("JUnit test for getAllMedicinesByPatientId- when no medicines exist")
    @Test
    void testGetAllMedicinesByPatientId_whenNoMedicinesExist() {
        // Arrange: Return an empty list from the repository
        when(medicineRepository.findAllByPatientId(1L)).thenReturn(Collections.emptyList());

        // Act: Call the service method
        List<MedicineDto> medicineDtosList = medicineService.getAllMedicineByPatientId(1L);

        // Assert: Verify that the returned list is empty
        assertThat(medicineDtosList).isNotNull();
        assertThat(medicineDtosList.isEmpty()).isTrue();

        // Verify that findAll() was called exactly once
        verify(medicineRepository, times(1)).findAllByPatientId(anyLong());
    }
    @DisplayName("Junit test for updateMedicineById method - positive case")
    @Test
    void testUpdateMedicineById_whenIdExist(){
        //Arrange
        MedicineDto updatedMedicineDto = MedicineDto.builder()
                .id("1")
                .name("Torglip M50/1000") // Previously it was "Torglip M 50/500"
                .dosage("1 tab")
                .duration("Morning only") // Previously It was "Morning & Night"
                .build();
        when(medicineRepository.findById(5L)).thenReturn(Optional.of(medicine));
        when(medicineRepository.save(any(Medicine.class))).thenReturn(medicine);

        // Act
        medicineService.updateMedicine(5L,updatedMedicineDto);

        // Assert
        assertThat(medicine).isNotNull();
        assertThat(medicine.getName()).isEqualTo("Torglip M50/1000");
        assertThat(medicine.getDuration()).isEqualTo("Morning only");

        // Verify the methodCalls
        verify(medicineRepository, times(1)).findById(anyLong());
        verify(medicineRepository, times(1)).save(medicine);
    }


    @DisplayName("JUnit test for updateMedicineById method - negative case")
    @Test
    void testUpdateMedicineById_whenIdDoesNotExist() {
        // Arrange
        Long inValidMedicineId = 200L;
        when(medicineRepository.findById(inValidMedicineId))
                .thenThrow(new NoSuchElementException("Medicine with id " + inValidMedicineId + " not found"));

        // Act & Assert
        assertThrows(NoSuchElementException.class,() ->
                medicineService.updateMedicine(inValidMedicineId, medicineDto) );

        // Verify
        verify(medicineRepository, times(1)).findById(anyLong());
        verify(medicineRepository, never()).save(any(Medicine.class));
    }

    @DisplayName("JUnit test for deleteMedicineById - positive case")
    @Test
    void testDeleteMedicineById_whenIdExists() {
        // Arrange
        Long medicineId = 5L;
        when(medicineRepository.existsById(medicineId)).thenReturn(true);

        // Act
        medicineService.deleteMedicineById(medicineId);

        // Assert: Verify that the delete methods are called with the correct patient ID
        verify(medicineRepository, times(1)).existsById(medicineId);
        verify(medicineRepository, times(1)).deleteById(medicineId);
    }
    @DisplayName("JUnit test for deleteMedicineById - negative case")
    @Test
    void testDeleteMedicineById_whenIdDoesNotExists() {
        // Arrange
        Long inValidId = 5L;
        when(medicineRepository.existsById(inValidId)).thenReturn(false);

        // Act
        medicineService.deleteMedicineById(inValidId);

        // Assert: Verify that no delete operations were performed
        verify(medicineRepository, times(1)).existsById(anyLong());
        verify(medicineRepository, never()).deleteById(anyLong());
    }
}
