package com.healthcare.doctor_consultation_medicine.repository;


import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
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
public class PatientRepositoryTest {
    @Autowired
    PatientRepository patientRepository;
    Patient patient;

    @BeforeEach
    public void setUp(){
        List<Medicine> medicineList = List.of(
                new Medicine(1L,"Paracetamol","Ten ml","Morn-After-Night",null,null),
                new Medicine(2L, "Amoxicillin", "500 mg", "Morning and Night", null, null),
                new Medicine(3L, "Cetrizine", "5 ml", "Once at Night", null, null)
        );

        patient = Patient.builder()
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
                .medicines(medicineList)
                .emergencyContactRelationship("Son")
                .emergencyContactNumber(9876543210L)
                .build();
    }

    @DisplayName(value = "JUnit test for findById method - positive case")
    @Test
    //Positive response test case
    public void testFindById(){
        // Arrange - Insert a test patient into the in-memory DB
        Patient savedPatient = patientRepository.save(patient);
        Long patientId = savedPatient.getId();

        // Act - Test the repository method
        Optional<Patient> retrievedPatient = patientRepository.findById(patientId);

        // Assert - Verify the result
        assertThat(retrievedPatient.isPresent()).isTrue();
        retrievedPatient.ifPresent(value -> {
                    assertThat(value.getEmail()).isEqualTo("alexbell@gmail.com");
                    assertThat(value).isEqualTo(patient);
                }
        );
    }
    @DisplayName(value = "JUnit test for findById method - negative case")
    @Test
    public void testFindById_NotFound(){

        Optional<Patient> retrievedPatient = patientRepository.findById(10L);

        assertThat(retrievedPatient).isEmpty();
    }

    @DisplayName(value = "JUnit test for findOneByEmail method - Positive case")
    @Test
    //Positive response test case
    public void testFindOneByEmail(){
        // Arrange - Insert a test patient into the in-memory DB
        patientRepository.save(patient);

        // Act - Test the repository method
        Optional<Patient> retrievedPatient = patientRepository.findOneByEmail("alexbell@gmail.com");

        // Assert - Verify the result
        assertThat(retrievedPatient.isPresent()).isTrue();
        retrievedPatient.ifPresent(value -> {
                    assertThat(value.getEmail()).isEqualTo("alexbell@gmail.com");
                    assertThat(value).isEqualTo(patient);
                    }
                );
    }
    //Negative response test case
    @DisplayName(value = "JUnit test for findOneByEmail method - Negative case")
    @Test
    public void testFindOneByEmail_NotFound() {
        // Act - Try to find a patient by an email that does not exist
        Optional<Patient> retrievedPatient = patientRepository.findOneByEmail("nonexist@gmail.com");

        // Assert - Verify that no patient is found
        assertThat(retrievedPatient).isEmpty();
    }


    @DisplayName(value = "JUnit test for saveNewPatient method")
    @Test
    public void testSavePatient(){
        Patient savedPatient = patientRepository.save(patient);

        assertThat(savedPatient).isNotNull();
        assertThat(savedPatient.getId()).isGreaterThan(0L);
        assertThat(savedPatient).isEqualTo(patient);
    }
    @DisplayName(value = "JUnit test for saveNewPatient with missing email (Negative case)")
    @Test
    public void testSavePatient_InvalidPatient() {
        // Arrange - Create a Patient object with missing email (invalid data)
        Patient invalidPatient = new Patient();
        invalidPatient.setFirstName("Abdul");
        invalidPatient.setLastName("Kalam");
        // invalidPatient.setEmail("apj@gmail.com"); Email is missing, which may cause an exception as It should be Unique and available

        // Act & Assert - Expecting an exception or invalid addAppointment attempt
        assertThrows(DataIntegrityViolationException.class, () -> {
            patientRepository.save(invalidPatient); // This should throw an exception
        });
    }

    @DisplayName(value = "JUnit test for saveNewPatient with duplicate email (Negative case)")
    @Test
    public void testSavePatient_DuplicateEmail() {
        // Arrange - Create and addAppointment the first patient
        Patient firstPatient = new Patient();
        firstPatient.setFirstName("Abdul");
        firstPatient.setLastName("Kalam");
        firstPatient.setEmail("apj@gmail.com");
        patientRepository.save(firstPatient);

        // Act & Assert - Try to addAppointment another Patient
        // with the same email
        Patient duplicatePatient = new Patient();
        duplicatePatient.setFirstName("Ajith");
        duplicatePatient.setLastName("Kumar");
        duplicatePatient.setEmail("apj@gmail.com"); // Same email as firstDoctor

        assertThrows(DataIntegrityViolationException.class, () -> {
            patientRepository.save(duplicatePatient); // Should throw an exception due to unique constraint
        });
    }
    @DisplayName(value = "JUnit test for existById method - positive case")
    @Test
    public void givenPatientId_whenExistsById_thenReturnTrue(){
        //Arrange or Given
        Long id  = patientRepository.save(patient).getId();
        //Act or When
        boolean result = patientRepository.existsById(id);
        //Assert or Then
        assertThat(result).isTrue();
    }
    @DisplayName(value = "JUnit test for existsById - negative case")
    @Test
    public void givenInvalidPatientId_whenExistsById_thenReturnFalse(){

        Long invalidPatientId = 999L;

        boolean result = patientRepository.existsById(invalidPatientId);

        assertThat(result).isFalse();
    }
    @DisplayName(value = "JUnit test for deleteById - positive case")
    @Test
    public void givenPatientId_whenDeleteById_thenRemovePatient(){

        Long savedId = patientRepository.save(patient).getId();

        patientRepository.deleteById(savedId);
        Optional<Patient> retrivedPatient = patientRepository.findById(savedId);

        assertThat(retrivedPatient).isEmpty();
    }
    @DisplayName(value = "JUnit test for deleteById - negative case")
    @Test
    public void givenInvalidPatientId_whenDeleteById_thenNoExceptionThrown() {
        // Arrange
        Long invalidPatientId = 999L;

        // Act
        assertDoesNotThrow(() -> {
            patientRepository.deleteById(invalidPatientId);
        });

        // Assert - Verify that the patient repository still has no entry for the invalid ID
        Optional<Patient> retrievedPatient = patientRepository.findById(invalidPatientId);
        assertThat(retrievedPatient).isEmpty();
    }

//These below methods are used in patient repository layer to fetch required data from database
//patientRepository.findById(id)
//patientRepository.findOneByEmail(email);
//patientRepository.addAppointment(newPatient);
//patientRepository.existsById(id);
//patientRepository.deleteById(id);

}
