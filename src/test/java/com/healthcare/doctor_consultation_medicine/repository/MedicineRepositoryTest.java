package com.healthcare.doctor_consultation_medicine.repository;

import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class MedicineRepositoryTest {
    @Autowired
    MedicineRepository medicineRepository;

    @Autowired
    PatientRepository patientRepository;
    Medicine firstMedicine,secondMedicine;
    Patient patient;
    Long savedPatientId;

    @BeforeEach
    public void setUp(){
        // Setting up a patient
        patient = Patient.builder()
                .firstName("Ratna")
                .lastName("Vel")
                .email("ratnavel@gmail.com")
                .build();

        savedPatientId = patientRepository.save(patient).getId();

        // Setting up first Medicine for the patient
        firstMedicine = Medicine.builder()
                .name("Paracetamol")
                .dosage("10 ml")
                .duration("Morning")
                .patient(patient)
                .build();


        // Setting up second Medicine for the same patient
        secondMedicine = Medicine.builder()
                .name("Telma")
                .dosage("40 mg")
                .duration("Morning")
                .patient(patient)
                .build();

    }
    @DisplayName(value = "JUnit test for saveNewMedicine method")
    @Test
    public void testSaveMedicine(){
        Medicine savedMedicine = medicineRepository.save(firstMedicine);

        assertThat(savedMedicine).isNotNull();
        assertThat(savedMedicine.getId()).isGreaterThan(0);
        assertThat(savedMedicine).isEqualTo(firstMedicine);
    }
    @DisplayName(value = "JUnit test for saveNewMedicine with missing name and duration (Negative case)")
    @Test
    public void testSaveMedicine_InvalidMedicine() {
        // Arrange - Create a firstMedicine with missing firstMedicine name and duration
        Medicine invalidMedicine = new Medicine();
        invalidMedicine.setName("Paracetamol");
        // name and duration is missing, which may cause an exception as It should be available and unique

        // Act & Assert - Expecting an exception or invalid addAppointment attempt
        assertThrows(DataIntegrityViolationException.class, () -> {
            medicineRepository.save(invalidMedicine); // This should throw an exception
        });
    }
    @DisplayName(value = "JUnit test for saveNewMedicine with duplicate firstMedicine name (Negative case)")
    @Test
    public void testSaveMedicine_DuplicateMedicine() {
        // Arrange - Create and addAppointment the first firstMedicine
        medicineRepository.save(firstMedicine);

        // Act & Assert - Try to addAppointment another firstMedicine with the same email
        Medicine duplicateMedicine = new Medicine();
        duplicateMedicine.setName("Paracetamol");
        duplicateMedicine.setDosage("10 ml");
        duplicateMedicine.setDuration("Morning");

        assertThrows(DataIntegrityViolationException.class, () -> {
            medicineRepository.save(duplicateMedicine); // Should throw an exception due to unique constraint
        });
    }
    @DisplayName(value = "Junit test for retrieving firstMedicine by id - positive case")
    @Test
    public void testFindById(){
        Medicine savedMedicine = medicineRepository.save(firstMedicine);
        Long savedMedicineId = savedMedicine.getId();

        Optional<Medicine> retrievedMedicine = medicineRepository.findById(savedMedicineId);

        assertThat(retrievedMedicine).isNotEmpty();
        retrievedMedicine.ifPresent(fetchedMedicine -> {
            assertThat(fetchedMedicine.getId()).isEqualTo(savedMedicineId);
            assertThat(fetchedMedicine).isEqualTo(firstMedicine);
        });
    }

    @DisplayName(value = "JUnit test for findByID - negative case")
    @Test
    public void testFindById_notFound() {
        // Arrange
        Long invalidMedicineId = 999L; // Not Actually present in the database

        // Act
        Optional<Medicine> retrievedMedicine = medicineRepository.findById(invalidMedicineId);

        // Assert
        assertThat(retrievedMedicine).isEmpty();
    }

    @DisplayName(value = "JUnit test for retrieving all medicines by using PatientId - positive case")
    @Test
    public void testFindAllByPatientId() {
        //Arrange
        medicineRepository.save(firstMedicine);
        medicineRepository.save(secondMedicine);

        // Act
        List<Medicine> allRetrievedMedicines = medicineRepository.findAllByPatientId(savedPatientId);

        // Assert
        assertThat(allRetrievedMedicines).isNotNull();
        assertThat(allRetrievedMedicines.size()).isEqualTo(2);

        // Verify first firstMedicine
        assertThat(allRetrievedMedicines.get(0).getPatient().getId()).isEqualTo(savedPatientId);
        assertThat(allRetrievedMedicines.get(0).getName()).isEqualTo("Paracetamol");

        // Verify second firstMedicine
        assertThat(allRetrievedMedicines.get(1).getPatient().getId()).isEqualTo(savedPatientId);
        assertThat(allRetrievedMedicines.get(1).getName()).isEqualTo("Telma");
    }
    @DisplayName(value = "JUnit test for retrieving medicines by non-existing PatientId - negative case")
    @Test
    public void testFindAllByNonExistingPatientId() {
        // Arrange
        Long nonExistingPatientId = 999L; // ID that does not exist in the DB

        // Act
        List<Medicine> allRetrievedMedicines = medicineRepository.findAllByPatientId(nonExistingPatientId);

        // Assert
        assertThat(allRetrievedMedicines).isNotNull();
        assertThat(allRetrievedMedicines.isEmpty()).isTrue(); // Verify that no medicines were retrieved
    }

    @DisplayName(value = "JUnit test for deleteById - positive case")
    @Test
    public void testDeleteById(){

        Long savedId = medicineRepository.save(firstMedicine).getId();

        medicineRepository.deleteById(savedId);
        Optional<Medicine> retrievedMedicine = medicineRepository.findById(savedId);

        assertThat(retrievedMedicine).isEmpty();
    }
    @DisplayName(value = "JUnit test for deleteById - negative case")
    @Test
    public void testDeleteById_NotFound() {
        // Arrange
        Long invalidMedicineId = 999L;

        // Act
        assertDoesNotThrow(() -> {
            medicineRepository.deleteById(invalidMedicineId);
        });

        // Assert - Verify that the firstMedicine repository still has no entry for the invalid ID
        Optional<Medicine> retrievedMedicine = medicineRepository.findById(invalidMedicineId);
        assertThat(retrievedMedicine).isEmpty();
    }

    @DisplayName(value = "JUnit test for deleteByPatientId - positive case")
    @Test
    public void testDeleteByPatientId(){

        medicineRepository.save(firstMedicine);
        medicineRepository.save(secondMedicine);

        medicineRepository.deleteByPatientId(savedPatientId);
        List<Medicine> allRetrievedMedicine = medicineRepository.findAllByPatientId(savedPatientId);

        assertThat(allRetrievedMedicine.isEmpty()).isTrue();
    }
    @DisplayName(value = "JUnit test for deleteByPatientId - negative case")
    @Test
    public void testDeleteByPatientId_notExist(){
        Long nonExistPatientId= 999L;

        assertDoesNotThrow(() -> {
            medicineRepository.deleteByPatientId(nonExistPatientId);
        });
        List<Medicine> allRetrievedMedicine = medicineRepository.findAllByPatientId(savedPatientId);

        assertThat(allRetrievedMedicine.isEmpty()).isTrue();
    }
}
//medicineRepository.findById(doctorId)
//medicineRepository.findAllByPatientId(patientId);
//medicineRepository.addAppointment
//medicineRepository.deleteById(id)
//medicineRepository.deleteByPatientId(id)
