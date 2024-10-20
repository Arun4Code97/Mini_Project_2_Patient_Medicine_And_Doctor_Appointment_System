package com.healthcare.doctor_consultation_medicine.repository;

import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Model.Patient;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class AppointmentRepositoryTest {
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DoctorRepository doctorRepository;
    Doctor doctor,savedDoctor;
    Patient patient,savedPatient;
    Appointment appointment;

@BeforeEach
public void setUp(){
    patient = Patient.builder()
            .firstName("Ratna")
            .lastName("Vel")
            .email("patient1@gmail.com")
            .build();
    savedPatient = patientRepository.save(patient);

    doctor = Doctor.builder()
            .firstName("Dr. Ajaya")
            .lastName("Nand Jha")
            .email("doctor1@gmail.com")
            .build();
    savedDoctor = doctorRepository.save(doctor);

    appointment = Appointment.builder()
    .appointmentDate(LocalDate.of(2024,10,14))
    .appointmentTime(LocalTime.of(10,30))
    .patient(savedPatient)
    .doctor(savedDoctor)
    .build();
//    System.out.println("\n\n\nLocal date from test is : \t" + LocalDate.now() +
//            "\nLocalDate.of(2024,10,14) : \t" + LocalDate.of(2024,10,14) +
//            "\nLocalTime.of(10,30) : \t" + LocalTime.of(23,59));
}
    @DisplayName(value = "JUnit test for saveNewAppointment method")
    @Test
    public void testSaveAppointment(){
        Appointment savedAppointment= appointmentRepository.save(appointment);

        assertThat(savedAppointment).isNotNull();
        assertThat(savedAppointment.getId()).isGreaterThan(0);
        assertThat(savedAppointment).isEqualTo(appointment);
    }
    @DisplayName(value = "JUnit test for saveNewAppointment with missing time (Negative case)")
    @Test
    public void testSaveAppointment_inValidAppointment() {
        Appointment invalidAppointment = Appointment.builder()
                .appointmentDate(LocalDate.now()).build();


        // Act & Assert - Expecting an exception or invalid addAppointment attempt
        assertThrows(DataIntegrityViolationException.class, () -> {
            appointmentRepository.save(invalidAppointment); // This should throw an exception
        });
    }

    @DisplayName(value = "Junit test for retrieving appointment by id - positive case")
    @Test
    public void testFindById(){
        Appointment savedAppointment = appointmentRepository.save(appointment);
        Long savedAppointmentId = savedAppointment.getId();

        Optional<Appointment> retrievedAppointment = appointmentRepository.findById(savedAppointmentId);

        assertThat(retrievedAppointment).isNotEmpty();
        retrievedAppointment.ifPresent(fetchedMedicine -> {
            assertThat(fetchedMedicine.getId()).isEqualTo(savedAppointmentId);
            assertThat(fetchedMedicine).isEqualTo(appointment);
        });
    }
    @DisplayName(value = "JUnit test for findByID - negative case")
    @Test
    public void testFindById_notFound() {
        // Arrange
        Long invalidAppointmentId = 999L; // Not Actually present in the database

        // Act
        Optional<Appointment> retrievedAppointment = appointmentRepository.findById(invalidAppointmentId);

        // Assert
        assertThat(retrievedAppointment).isEmpty();
    }

    @DisplayName(value = "JUnit test for retrieving all appointments by using DoctorId & Date - positive case")
    @Test
    public void testFindByDoctorIdAndAppointmentDate() {
    //On 2024-10-14 -> patient1 books an appointment at 10:30
        appointmentRepository.save(appointment);

    //On 2024-10-14 -> different patient books appointment with the same doctor at 11
        Patient differentPatient =  Patient.builder()
                .firstName("Sathish")
                .lastName("M")
                .email("patient2@gmail.com")
                .build();
        Patient savedDifferentPatient = patientRepository.save(differentPatient);

        Appointment anotherAppointmentWithDifferentPatient = Appointment.builder()
                .appointmentDate(LocalDate.of(2024,10,14)) //on 14th date and different patient books appointment with the same doctor
                .appointmentTime(LocalTime.of(11,0))
                .patient(savedDifferentPatient)
                .doctor(savedDoctor)
                .build();
        appointmentRepository.save(anotherAppointmentWithDifferentPatient);

    //On 2024-10-16 -> same patient books appointment with the same doctor -> follow up/revisit
        Appointment anotherAppointmentOnDifferentDate = Appointment.builder()
                .appointmentDate(LocalDate.of(2024,10,16))
                .appointmentTime(LocalTime.of(10,30))
                .patient(savedPatient)
                .doctor(savedDoctor)
                .build();
        appointmentRepository.save(anotherAppointmentOnDifferentDate);

//        ------------------ Testing FirstCase --> Total Appointments = 2 -----------------
        // Act
        List<Appointment> allRetrievedAppointmentsForFirstCase = appointmentRepository.findByDoctorIdAndAppointmentDate(
                savedDoctor.getId(), LocalDate.of(2024,10,14));

        // Assert
        assertThat(allRetrievedAppointmentsForFirstCase).isNotNull();
        assertThat(allRetrievedAppointmentsForFirstCase.size()).isEqualTo(2);

        // Verify firstCase
        assertThat(allRetrievedAppointmentsForFirstCase.get(0).getPatient().getId()).isEqualTo(savedPatient.getId());
        assertThat(allRetrievedAppointmentsForFirstCase.get(1).getPatient().getId()).isEqualTo(savedDifferentPatient.getId());

        //        ------------------ Testing SecondCase --> Total Appointments = 1 -----------------
        // Act
        List<Appointment> allRetrievedAppointmentsForSecondCase = appointmentRepository.findByDoctorIdAndAppointmentDate(
                savedDoctor.getId(), LocalDate.of(2024,10,16));

        // Assert
        assertThat(allRetrievedAppointmentsForSecondCase).isNotNull();
        assertThat(allRetrievedAppointmentsForSecondCase.size()).isEqualTo(1);

        // Verify secondCase
        assertThat(allRetrievedAppointmentsForSecondCase.get(0).getPatient().getId()).isEqualTo(savedPatient.getId());
}

    @DisplayName(value = "JUnit test for retrieving an appointment by using DoctorId,Date & Time - positive case")
    @Test
    public void testFindByDoctorIdAndAppointmentDateAndAppointmentTime() {
        //On 2024-10-14 -> patient1 books an appointment at 10:30
        appointmentRepository.save(appointment);

        // Act
        Appointment retrievedAppointment = appointmentRepository.findByDoctorIdAndAppointmentDateAndAppointmentTime(
                savedDoctor.getId(),
                LocalDate.of(2024,10,14),
                LocalTime.of(10,30)
                );

        // Assert
        assertThat(retrievedAppointment).isNotNull();
        assertThat(retrievedAppointment.getDoctor().getId()).isEqualTo(savedDoctor.getId());
        assertThat(retrievedAppointment.getAppointmentDate()).isEqualTo(LocalDate.of(2024,10,14));
        assertThat(retrievedAppointment.getAppointmentTime()).isEqualTo(LocalTime.of(10,30));

    }
}
//  appointmentRepository.addAppointment(savedAppointment);
//  appointmentRepository.findById(appointmentId);
//  appointmentRepository.findByDoctorIdAndAppointmentDate(doctorId,appointmentDate);
//  appointmentRepository.findByDoctorIdAndAppointmentDateAndAppointmentTime(doctorId,date,time);



