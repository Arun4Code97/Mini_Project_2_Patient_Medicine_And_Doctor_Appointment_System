package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;


@Controller
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

    // Save medicine to the database
    @PostMapping("/doctorPortal/{doctorId}/addMedicine/{patientId}/{appointmentId}")
    public String addMedicine(@PathVariable("doctorId") Long doctorId,
                              @PathVariable("patientId") Long patientId,
                              @PathVariable("appointmentId") Long appointmentId,
                              @ModelAttribute("medicine") MedicineDto medicine) {
        MedicineDto savedMedicine = medicineService.addMedicine(doctorId, patientId,medicine);
        Appointment appointment = appointmentService.findById(appointmentId);
        String timeStr = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        return "redirect:/appointment/goToConsultationRoom?doctorId=" + doctorId + "&date="+appointment.getAppointmentDate()+"&time="+ timeStr;
    }
    @GetMapping("medicine/get/{id}") //For AJAX call in javascript
    public ResponseEntity<MedicineDto> getMedicineById(@PathVariable Long id) {
        MedicineDto medicine = medicineService.getSingleMedicineById(id);
        if (medicine != null ) {
            MedicineDto clonedMedicine = new MedicineDto(
                    medicine.getId(),medicine.getName(),medicine.getDosage(),medicine.getDuration(),null,null);
            return ResponseEntity.ok(clonedMedicine);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/hospital/patientPortal/{id}/medicines")
    public String toShowMedicinesRequest(@PathVariable Long id , Model model){
        model.addAttribute("mode","medicineView");
        model.addAttribute("medicineView", "fragmentName");
        model.addAttribute("patient",patientService.getSinglePatientById(id));
        return "patient/patientPortal";
    }
    // Update the medicine in the database
    @PostMapping("/doctorPortal/{doctorId}/updateMedicine/{appointmentId}")
    public String updateMedicine(@PathVariable("doctorId") Long doctorId,
                                 @PathVariable("appointmentId") Long appointmentId,
                                 @ModelAttribute("medicine") MedicineDto updatedMedicine) {
        MedicineDto updatedMedicineDto = medicineService.updateMedicine(
                Long.parseLong(updatedMedicine.getId()), updatedMedicine);

        Appointment appointment = appointmentService.findById(appointmentId);
        if (appointment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found");
        }
        String timeStr = appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        return "redirect:/appointment/goToConsultationRoom?doctorId=" + doctorId + "&date="
            + appointment.getAppointmentDate() + "&time=" + timeStr;
    }
    @GetMapping("/deleteMedicine/{medicineId}")     // Delete medicine-> AJAX call from javascript
    public ResponseEntity<String> deleteMedicineById(@PathVariable Long medicineId) {
        if (medicineId != null ) {
            medicineService.deleteMedicineById(medicineId);
            return ResponseEntity.ok("deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
// Display form to add medicine
//    @GetMapping("/doctorPortal/{doctorId}/addMedicine/{patientId}")
//    public String showAddMedicineForm(@PathVariable("doctorId") Long doctorId, @PathVariable("patientId") Long patientId, Model model) {
//        model.addAttribute("medicine", new MedicineDto());
//        model.addAttribute("doctor", doctorService.getSingleDoctorById(doctorId));
//        model.addAttribute("patient", patientService.getSinglePatientById(patientId));
//        return "doctor-medicine/addMedicine";
//    }



// Display form to update medicine
//    @GetMapping("/doctorPortal/{doctorId}/updateMedicine/{medicineId}")
//    public String showUpdateMedicineForm(@PathVariable Long doctorId, @PathVariable Long medicineId, Model model) {
//        MedicineDto medicine = medicineService.getSingleMedicineById(medicineId);
//        model.addAttribute("medicine", medicine);
//        model.addAttribute("doctorId", doctorId);
//        return "doctor-medicine/updateMedicine";
//    }