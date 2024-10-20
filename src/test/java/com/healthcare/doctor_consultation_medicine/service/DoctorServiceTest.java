package com.healthcare.doctor_consultation_medicine.service;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Mapper.DoctorMapper;
import com.healthcare.doctor_consultation_medicine.Mapper.MedicineMapper;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Service.Implementation.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    @Mock
    DoctorRepository doctorRepository;
    @Mock
    AppointmentRepository appointmentRepository;
    @Mock
    MedicineRepository medicineRepository;

    @InjectMocks
    DoctorServiceImpl doctorService;

    Doctor doctor;
    DoctorDto doctorDto;

    @BeforeEach
    public void setUp(){
        List<Medicine> medicineList = List.of(
                new Medicine(1L,"Paracetamol","10 ml","Morn-After-Night",null,null),
                new Medicine(2L, "Amoxicillin", "500 mg", "Morning and Night", null, null),
                new Medicine(3L, "Cetrizine", "5 ml", "Once at Night", null, null)
        );

        doctor = Doctor.builder()
                .id(1L)
                .firstName("Dr. Ajaya")
                .lastName("Nand Jha")
                .gender("Male")
                .email("doctor1@gmail.com")
                .specialization("Neurosurgery")
                .qualification("MBBS,MS,FRCS")
                .experience(42)
                .phoneNumber(9876543210L)
                .medicines(medicineList)
                .build();
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
    @DisplayName("Junit test for testSaveDoctorWithImage method - positive case")
    @Test
    public void testSaveDoctorWithImage(){
        //Arrange
        try(MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)) {
           mapperMockedStatic.when(() -> DoctorMapper.mapToEntity(any(DoctorDto.class)))
                   .thenReturn(doctor);
           when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);
           mapperMockedStatic.when( () -> DoctorMapper.mapToDto(any(Doctor.class)))
                   .thenReturn(doctorDto);

        //Act
            DoctorDto savedDoctor = doctorService.saveDoctorWithImage(doctorDto);

        //Assert
            assertThat(savedDoctor).isNotNull();
            assertThat(savedDoctor.getEmail()).isEqualTo("doctor1@gmail.com");
            mapperMockedStatic.verify(() -> DoctorMapper.mapToEntity(any(DoctorDto.class)),times(1));
            verify(doctorRepository,times(1)).save(any(Doctor.class));
            mapperMockedStatic.verify(() -> DoctorMapper.mapToDto(any(Doctor.class)), times(1));
        }
    }

    @DisplayName("Junit test for testSaveDoctorWithImage method - Negative case")
    @Test
    public void testSaveDoctorWithImage_dataBaseException(){
        //Arrange
        DoctorDto invalidDoctor = DoctorDto.builder()
                .firstName(null)
                .lastName(null)
                .email("invalid@gmail.com")
                .build();

        try(MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)) {
            mapperMockedStatic.when(() -> DoctorMapper.mapToEntity(any(DoctorDto.class)))
                    .thenReturn(doctor);
            when(doctorRepository.save(any(Doctor.class)))
                    .thenThrow(new DataIntegrityViolationException("Null and Unique constraint violation"));
            mapperMockedStatic.when( () -> DoctorMapper.mapToDto(any(Doctor.class)))
                    .thenReturn(doctorDto);

        //Act & Assert
            assertThrows(DataIntegrityViolationException.class, () -> {
                DoctorDto savedDoctor = doctorService.saveDoctorWithImage(invalidDoctor);
                assertThat(savedDoctor).isNotNull();
            });

        //Verify
            mapperMockedStatic.verify(() -> DoctorMapper.mapToEntity(any(DoctorDto.class))
                    ,times(1));
            verify(doctorRepository,times(1)).save(any(Doctor.class));
        }
    }
    @DisplayName("Junit test for testSetPassword method - positive case")
    @Test
    void testSetPassword() {
        // Arrange: Mock repository behavior to return a doctor for a valid patientId
        when(doctorRepository.findById(5L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        // Act
        doctorService.setPassword(5L, "newPassword"); // Reset with newPassword

        // Assert
        assertThat(doctor.getPassword()).isNotNull(); // After successful addAppointment, password field is set as newPassword
        // Verify
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }
    @DisplayName("Junit test for testSetPassword method - negative case")
    @Test
    void testSetPassword_inValidPatientId() {
        // Arrange:
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        doctorService.setPassword(99L, "newPassword"); // Reset with newPassword

        // Assert
        assertThat(doctor.getPassword()).isNull();
        verify(doctorRepository, never()).save(any(Doctor.class));

    }
    @DisplayName("Junit test for testExistByEmailId method - positive case")
    @Test
    public void testExistByEmailId(){
        //Arrange
        when(doctorRepository.findOneByEmail("doctor1@gmail.com")).thenReturn(Optional.of(doctor));

        //Act
        boolean isDoctorExist = doctorService.isExistByEmail("doctor1@gmail.com");

        //Assert
        assertThat(isDoctorExist).isTrue();
    }
    @DisplayName("Junit test for testExistByEmailId method - negative case")
    @Test
    public void testExistByEmailId_doesNotExist(){
        //Arrange
        when(doctorRepository.findOneByEmail("nonexist@gmail.com")).thenReturn(Optional.empty());

        //Act
        boolean isDoctorExist = doctorService.isExistByEmail("nonexist@gmail.com");

        //Assert
        assertThat(isDoctorExist).isFalse();
    }
    @DisplayName("Junit test for testExistById method - positive case")
    @Test
    public void testExistById(){
        //Arrange
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        //Act
        boolean isDoctorExist = doctorService.isExistById(1L);

        //Assert
        assertThat(isDoctorExist).isTrue();
    }
    @DisplayName("Junit test for testExistById method - negative case")
    @Test
    public void testExistById_doesNotExist(){
        //Arrange
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        //Act
        boolean isDoctorExist = doctorService.isExistById(99L);

        //Assert
        assertThat(isDoctorExist).isFalse();
    }
    @DisplayName("Junit test for getSingleDoctorById method - positive case")
    @Test
    void getSingleDoctorById(){
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        try(MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)){
            mapperMockedStatic.when(() -> DoctorMapper.mapToDto(any(Doctor.class)))
                    .thenReturn(doctorDto);

            DoctorDto retrievedDoctorDto  = doctorService.getSingleDoctorById(1L);

            assertThat(retrievedDoctorDto ).isNotNull();
            assertThat(retrievedDoctorDto.getId()).isEqualTo("1");
            assertThat(retrievedDoctorDto.getEmail()).isEqualTo("doctor1@gmail.com");

            verify(doctorRepository, times(1)).findById(1L);
            mapperMockedStatic.verify(() -> DoctorMapper.mapToDto(any(Doctor.class))
                    ,times(1));
        }
    }
    @DisplayName("Junit test for getSingleDoctorById method - negative case")
    @Test
    void testGetSingleDoctorById_forInvalidDoctorId() {
        // Arrange: Mock repository to return Optional.empty() for the given id
        when(doctorRepository.findById(200L)).thenReturn(Optional.empty());

        try(MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)){
            mapperMockedStatic.when(() -> DoctorMapper.mapToDto(any(Doctor.class)))
                    .thenReturn(new DoctorDto());
        // Act: Call the service method with an invalid doctorId 200L
        DoctorDto retrievedDoctorDto = doctorService.getSingleDoctorById(200L);

        // Assert: Verify that retrievedDoctorDto is null
        assertThat(retrievedDoctorDto).isNull();

        // Verify repository was called
        verify(doctorRepository, times(1)).findById(200L);
        mapperMockedStatic.verify(() -> DoctorMapper.mapToDto(any(Doctor.class))
                    ,never());
         }
    }

    @DisplayName("Junit test for findDoctorByEmailId method - positive case")
    @Test
    void testFindDoctorByEmailId(){
        when(doctorRepository.findOneByEmail("doctor1@gmail.com")).thenReturn(Optional.of(doctor));

        try(MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)){

            mapperMockedStatic.when(() -> DoctorMapper.mapToDto(any(Doctor.class)))
                    .thenReturn(doctorDto);

            Optional<DoctorDto> retrivedDoctor = doctorService.findDoctorByEmailId("doctor1@gmail.com");

            assertThat(retrivedDoctor.isPresent()).isTrue();
            retrivedDoctor.ifPresent(doctorDto1 -> assertThat(doctorDto1.getEmail())
                    .isEqualTo("doctor1@gmail.com"));
        }
    }
    @DisplayName("Junit test for findDoctorByEmailId method - negative case")
    @Test
    void testFindDoctorByEmailId_nonExistEmail(){
        when(doctorRepository.findOneByEmail("nonexistdoctor@gmail.com"))
                .thenReturn(Optional.empty());

        try(MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)){

            mapperMockedStatic.when(() -> DoctorMapper.mapToDto(any(Doctor.class)))
                    .thenReturn(new DoctorDto());

            Optional<DoctorDto> retrivedDoctor = doctorService.findDoctorByEmailId("nonexistdoctor@gmail.com");

            assertThat(retrivedDoctor.isPresent()).isFalse();
        }
    }
    @DisplayName("JUnit test for getAllDoctors - positive case")
    @Test
    void testGetAllDoctors_whenDoctorsExist() {
        // Arrange: Set up the list of doctors returned by the repository
        List<Doctor> doctorList = Arrays.asList(
                doctor ,
                Doctor.builder().id(2L)
                        .firstName("doctor")
                        .lastName("two")
                        .email("doctor2@gmail.com")
                        .build()
        );

        when(doctorRepository.findAll()).thenReturn(doctorList);

        // Mock the static method behavior of DoctorMapper
        try (MockedStatic<DoctorMapper> mapperMockedStatic = mockStatic(DoctorMapper.class)) {
            mapperMockedStatic.when(() -> DoctorMapper.mapToDto(doctorList.get(0)))
                    .thenReturn(doctorDto);
            mapperMockedStatic.when(() -> DoctorMapper.mapToDto(doctorList.get(1)))
                    .thenReturn(DoctorDto.builder()
                            .id("2")
                            .firstName("doctor")
                            .lastName("two")
                            .email("doctor2@gmail.com")
                            .build());

            // Act: Call the method
            List<DoctorDto> doctorDtoList = doctorService.getAllDoctors();

            // Assert: Verify that the list of DTOs is correct
            assertThat(doctorDtoList).isNotNull();
            assertThat(doctorDtoList.size()).isEqualTo(2);
            assertThat(doctorDtoList.get(0).getFirstName()).isEqualTo("Dr. Ajaya");
            assertThat(doctorDtoList.get(0).getLastName()).isEqualTo("Nand Jha");
            assertThat(doctorDtoList.get(1).getFirstName()).isEqualTo("doctor");
            assertThat(doctorDtoList.get(1).getLastName()).isEqualTo("two");

            // Verify that findAll() was called exactly once
            verify(doctorRepository, times(1)).findAll();
        }
    }
    @DisplayName("JUnit test for getAllDoctors - when no doctors exist")
    @Test
    void testGetAllDoctors_whenNoDoctorsExist() {
        // Arrange: Return an empty list from the repository
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        // Act: Call the service method
        List<DoctorDto> doctorDtoList = doctorService.getAllDoctors();

        // Assert: Verify that the returned list is empty
        assertThat(doctorDtoList).isNotNull();
        assertThat(doctorDtoList.isEmpty()).isTrue();

        // Verify that findAll() was called exactly once
        verify(doctorRepository, times(1)).findAll();
    }

    // If doctor exists, all delete operations should be called
    @DisplayName("JUnit test for deleteDoctorById - positive case")
    @Test
    void testDeleteDoctorById_whenIdExists() {
        // Arrange
        Long doctorId = 5L;
        when(doctorRepository.existsById(doctorId)).thenReturn(true);

        // Act
        doctorService.deleteDoctorById(doctorId);

        // Assert: Verify that the delete methods are called with the correct doctor ID
        verify(appointmentRepository, times(1)).deleteByDoctorId(doctorId);
        verify(medicineRepository, times(1)).deleteByDoctorId(doctorId);
        verify(doctorRepository, times(1)).deleteById(doctorId);
    }
    @DisplayName("JUnit test for deleteDoctorById - negative case")
    @Test
    void testDeleteDoctorById_whenIdDoesNotExist() {
        // Arrange
        Long doctorId = 200L;
        when(doctorRepository.existsById(doctorId)).thenReturn(false);

        // Act
        doctorService.deleteDoctorById(doctorId);

        // Assert: Verify that no delete operations were performed
        verify(appointmentRepository, never()).deleteByDoctorId(anyLong());
        verify(medicineRepository, never()).deleteByDoctorId(anyLong());
        verify(doctorRepository, never()).deleteById(anyLong());
    }
}
