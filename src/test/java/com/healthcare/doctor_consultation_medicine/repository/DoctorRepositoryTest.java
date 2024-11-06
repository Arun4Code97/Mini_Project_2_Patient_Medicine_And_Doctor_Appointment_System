package com.healthcare.doctor_consultation_medicine.repository;

import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;

import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class DoctorRepositoryTest {
    @Autowired
    DoctorRepository doctorRepository;
    Doctor doctor;

    @BeforeEach
    public void setUp(){
        List<Medicine> medicineList = List.of(
                new Medicine(1L,"Paracetamol","10 ml","Morn-After-Night",null,null),
                new Medicine(2L, "Amoxicillin", "500 mg", "Morning and Night", null, null),
                new Medicine(3L, "Cetrizine", "5 ml", "Once at Night", null, null)
                 );

        doctor = Doctor.builder()
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
    }
    @DisplayName(value = "JUnit test for saveNewDoctor method")
    @Test
    public void testSaveDoctor(){
        Doctor savedDoctor = doctorRepository.save(doctor);

        assertThat(savedDoctor).isNotNull();
        assertThat(savedDoctor.getId()).isGreaterThan(0L);
        assertThat(savedDoctor.getEmail()).isEqualTo(doctor.getEmail());
        assertThat(savedDoctor).isEqualTo(doctor);
    }
    @DisplayName(value = "JUnit test for saveNewDoctor with missing email (Negative case)")
    @Test
    public void testSaveDoctor_InvalidDoctor() {
        // Arrange - Create a Doctor object with missing email (invalid data)
        Doctor invalidDoctor = new Doctor();
        invalidDoctor.setFirstName("John");
        invalidDoctor.setLastName("Doe");
        // Email is missing, which may cause an exception as It should be Unique and available

        // Act & Assert - Expecting an exception or invalid addAdviseToAppointment attempt
        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.save(invalidDoctor); // This should throw an exception
        });
    }

    @DisplayName(value = "JUnit test for saveNewDoctor with duplicate email (Negative case)")
    @Test
    public void testSaveDoctor_DuplicateEmail() {
        // Arrange - Create and addAdviseToAppointment the first doctor
        Doctor firstDoctor = new Doctor();
        firstDoctor.setFirstName("Abdul");
        firstDoctor.setLastName("Kalam");
        firstDoctor.setEmail("apj@gmail.com");
        doctorRepository.save(firstDoctor);

        // Act & Assert - Try to addAdviseToAppointment another doctor with the same email
        Doctor duplicateDoctor = new Doctor();
        duplicateDoctor.setFirstName("Ajith");
        duplicateDoctor.setLastName("Kumar");
        duplicateDoctor.setEmail("apj@gmail.com"); // Same email as firstDoctor

        assertThrows(DataIntegrityViolationException.class, () -> {
            doctorRepository.save(duplicateDoctor); // Should throw an exception due to unique constraint
        });
    }
    @DisplayName(value = "Junit test for retrieving doctor by id - positive case")
    @Test
    public void testFindById(){
        Doctor savedDoctor = doctorRepository.save(doctor);
        Long savedDoctorId = savedDoctor.getId();

        Optional<Doctor> retrievedDoctor = doctorRepository.findById(savedDoctorId);

        assertThat(retrievedDoctor).isNotEmpty();
        retrievedDoctor.ifPresent(tempDoctor -> {
            assertThat(retrievedDoctor.get().getId()).isEqualTo(savedDoctorId);
            assertThat(retrievedDoctor.get()).isEqualTo(doctor);
        });
    }
    @DisplayName(value = "JUnit test for findByID - negative case")
    @Test
    public void testFindById_notFound() {
        // Arrange
        Long invalidDoctorId = 999L; // Not Actually present in the database

        // Act
        Optional<Doctor> retrievedDoctor = doctorRepository.findById(invalidDoctorId);

        // Assert
        assertThat(retrievedDoctor).isEmpty();
    }
    @DisplayName(value = "JUnit test for findOneByEmail method - Positive case")
    @Test
    public void testFindOneByEmail(){
        // Arrange - Insert a test doctor into the in-memory DB
        doctorRepository.save(doctor);

        // Act - Test the repository method
        Optional<Doctor> retrievedDoctor = doctorRepository.findOneByEmail("doctor1@gmail.com");

        // Assert - Verify the result
        assertThat(retrievedDoctor.isPresent()).isTrue();
        assertThat(retrievedDoctor).isNotEmpty();
        retrievedDoctor.ifPresent(tempDoctor -> {
                    assertThat(tempDoctor.getEmail()).isEqualTo("doctor1@gmail.com");
                    assertThat(tempDoctor).isEqualTo(doctor);
                }
        );
    }
    //Negative response test case
    @DisplayName(value = "JUnit test for findOneByEmail method - Negative case")
    @Test
    public void testFindOneByEmail_NotFound() {
        // Act - Try to find a doctor by an email that does not exist
        Optional<Doctor> retrievedDoctor = doctorRepository.findOneByEmail("nonexist@gmail.com");

        // Assert - Verify that no doctor is found
        assertThat(retrievedDoctor).isEmpty();
    }
    @DisplayName(value = "JUnit test for existById method - positive case")
    @Test
    public void testExistById(){
        //Arrange
        Long id  = doctorRepository.save(doctor).getId();
        //Act
        boolean result = doctorRepository.existsById(id);
        //Assert
        assertThat(result).isTrue();
    }
    @DisplayName(value = "JUnit test for existsById - negative case")
    @Test
    public void testExistById_NotFound(){

        Long invalidDoctorId = 999L;// Not Actually present in the database

        boolean result = doctorRepository.existsById(invalidDoctorId);

        assertThat(result).isFalse();
    }
    @DisplayName(value = "JUnit test for deleteById - positive case")
    @Test
    public void testDeleteById(){

        Long savedId = doctorRepository.save(doctor).getId();

        doctorRepository.deleteById(savedId);
        Optional<Doctor> retrievedDoctor = doctorRepository.findById(savedId);

        assertThat(retrievedDoctor).isEmpty();
    }
    @DisplayName(value = "JUnit test for deleteById - negative case")
    @Test
    public void testDeleteById_NotFound() {
        // Arrange
        Long invalidDoctorId = 999L;

        // Act
        assertDoesNotThrow(() -> {
            doctorRepository.deleteById(invalidDoctorId);
        });

        // Assert - Verify that the doctor repository still has no entry for the invalid ID
        Optional<Doctor> retrievedDoctor = doctorRepository.findById(invalidDoctorId);
        assertThat(retrievedDoctor).isEmpty();
    }
}
