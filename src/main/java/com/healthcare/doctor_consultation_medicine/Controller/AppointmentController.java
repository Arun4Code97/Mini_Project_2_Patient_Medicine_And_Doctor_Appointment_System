package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import com.healthcare.doctor_consultation_medicine.Others.AppointmentSlots;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final MedicineService medicineService;

    @GetMapping("/schedule/{doctorId}/{patientId}")
    public String showFormForAvailableAppointmentSlots(@PathVariable("doctorId") Long doctorId,
                                                       @PathVariable("patientId") Long patientId,
                                                       @RequestParam(value = "date",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                       Model model){
        if (date == null) {
            date = LocalDate.now();
        }
        List<Appointment> bookedAppointmentList = appointmentService.collectBookedSlots(doctorId,date);

        DoctorDto doctor = doctorService.getSingleDoctorById(doctorId);

        List<String> formattedAppointments = bookedAppointmentList.stream()
                .map(appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        model.addAttribute("appointments",formattedAppointments);
        model.addAttribute("doctor",doctor );
        model.addAttribute("doctorBase64Image", Base64.getEncoder().encodeToString(doctor.getImage()));
        model.addAttribute("patient", patientService.getSinglePatientById(patientId));
        model.addAttribute("date", date);
        model.addAttribute("morningSlots", AppointmentSlots.morningSlots);
        model.addAttribute("eveningSlots", AppointmentSlots.eveningSlots);
        model.addAttribute("scheduleAppointment","fragmentName");

        return "patient/patientPortal";
    }

   @GetMapping("/book")
   public String bookAppointment(@RequestParam(name = "doctorId") Long doctorId,
                                 @RequestParam(name = "patientId") Long patientId,
                                 @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                 @RequestParam(name="time") String timeStr,
                                 Model model){

        // Manually parse the time string into LocalTime
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        Appointment appointment = appointmentService.bookAppointment(doctorId,patientId,date,time);

        model.addAttribute("appointment",appointment);
        model.addAttribute("patient", patientService.getSinglePatientById(patientId));
        model.addAttribute("scheduleAppointmentConfirmation","fragmentName");

        return "patient/patientPortal";
   }
    @GetMapping("/scheduled/{doctorId}")
    public String showFormForBookedAppointmentSlotsToDoctor(@PathVariable("doctorId") Long doctorId,
                                                            @RequestParam(value = "date",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                            Model model){

        if (date == null) {
            date = LocalDate.now();
        }

        List<Appointment> bookedAppointmentList = appointmentService.collectBookedSlots(doctorId,date);

        DoctorDto doctor = doctorService.getSingleDoctorById(doctorId);
        // To check appointment formatted time
        List<String> formattedBookedAppointmentsTime = bookedAppointmentList.stream()
                .map(appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());
        model.addAttribute("doctor",doctor );
        model.addAttribute("doctorBase64Image", Base64.getEncoder().encodeToString(doctor.getImage()));
        model.addAttribute("date", date);
        model.addAttribute("morningSlots", AppointmentSlots.morningSlots);
        model.addAttribute("eveningSlots", AppointmentSlots.eveningSlots);
        model.addAttribute("appointments",formattedBookedAppointmentsTime);
        model.addAttribute("showBookedAppointments","fragmentName");
        return "doctor/doctorPortal";
    }
    @GetMapping("/goToConsultationRoom")
    public String showConsultationRoomToDoctorForBookedAppointmentSlots(@RequestParam("doctorId") Long doctorId,
                                                                        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                        @RequestParam(name="time") String timeStr,
                                                   Model model) {
        // Manually parse the time string into LocalTime
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
//        System.out.println("After Formatting time as LocalTime : \t" + timeStr);
        PatientDto patient =appointmentService.fetchPatientRecord(doctorId,date,time);
        Appointment appointment = appointmentService.fetchAppointment(doctorId,date,time);


        List<MedicineDto> medicineList = new ArrayList<>();
        medicineList = medicineService.getAllMedicineByPatientId(Long.parseLong(patient.getId()));

        patient.setMedicines(medicineList);

        model.addAttribute("mode","view");
        model.addAttribute("patient",patient);
        model.addAttribute("doctor",doctorService.getSingleDoctorById(doctorId));
        model.addAttribute("doctorId",doctorId);
        model.addAttribute("medicine",new MedicineDto());
        model.addAttribute("medicineList",medicineList);
        if(medicineList.isEmpty())
            model.addAttribute("error","No medications prescribed by other doctors");
        model.addAttribute("appointmentId",appointment.getId());
        model.addAttribute("advice",appointment.getDoctorAdvice());
        model.addAttribute("showPatientByAppointmentTime","fragmentName");
        return "doctor/doctorPortal";
    }
    @PostMapping("/saveAdviceOnly")
    public String saveDoctorAdviceOnly(
            @RequestParam("advice") String advice,
            @RequestParam("appointmentId") Long appointmentId,Model model) {

        // Save the doctor's advice to the appointment entity
        Appointment appointment = appointmentService.findById(appointmentId);
        appointment.setDoctorAdvice(advice);

        appointmentService.addAppointment(appointment);
        List<MedicineDto> medicineList = new ArrayList<>();
        medicineList = medicineService.getAllMedicineByPatientId(appointment.getPatient().getId());

        model.addAttribute("patient",appointment.getPatient());
        model.addAttribute("doctor",appointment.getDoctor());
        model.addAttribute("doctorId",appointment.getDoctor().getId());
        model.addAttribute("advice",appointment.getDoctorAdvice());
        model.addAttribute("appointmentId",appointment.getId());
        model.addAttribute("medicineList",medicineList);
        model.addAttribute("medicine",new MedicineDto());
        model.addAttribute("mode","view");
        model.addAttribute("showPatientByAppointmentTime","fragmentName");

        return "doctor/doctorPortal";
    }
}
