package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointment")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorRepository doctorService;
    private final PatientService patientService;
    @GetMapping("/schedule/{doctorId}/{patientId}")
    public String showFormForAvailableAppointmentSlots(@PathVariable("doctorId") Long doctorId,
                                                       @PathVariable("patientId") Long patientId,
                                                       @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                       Model model){
        List<Appointment> appointmentList = appointmentService.collectBookedSlots(doctorId,date);
        System.out.println("\n\nAppointment List count : \t" + appointmentList.size());
//        for(Appointment appointment:appointmentList){
//            System.out.println("\n\n\n\nappointment : \t\t\t"+ appointment.getId());
//        }
        Doctor doctor = doctorService.findById(doctorId).get();
        model.addAttribute("doctor",doctor );
        model.addAttribute("doctorBase64Image", Base64.getEncoder().encodeToString(doctor.getImage()));
        model.addAttribute("patient", patientService.getPatientById(patientId).get());
        model.addAttribute("date", date);

        List<String> morningSlots = List.of("10:00","10:15","10:30","10:45","11:00");
        List<String> eveningSlots = List.of("06:00","06:15","06:30","06:45",
                                            "07:00","07:15","07:30","07:45","08:00");

        model.addAttribute("morningSlots",morningSlots);
        model.addAttribute("eveningSlots",eveningSlots);

        List<String> formattedAppointments = appointmentList.stream()
                .map(appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        model.addAttribute("appointments",formattedAppointments);
//        model.addAttribute("appointments",appointmentList.stream().map(appointment -> appointment.getAppointmentTime().toString()).toList());

        return "appointment/scheduleAppointment";
    }
   @GetMapping("/book")
   public String bookAppointment(@RequestParam(name = "doctorId") Long doctorId,
                                 @RequestParam(name = "patientId") Long patientId,
                                 @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                 @RequestParam(name="time") String timeStr,
                                 Model model){
       System.out.println("Before Formatting time : \t" + timeStr);
       // Manually parse the time string into LocalTime
       LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        Appointment appointment = appointmentService.bookAppointment(doctorId,patientId,date,time);
        model.addAttribute("appointment",appointment);
        model.addAttribute("doctorName",appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
        return "appointment/confirmationPage";
   }
    // Here doctor can click on show patient records Button only on already booked slots
    @GetMapping("/scheduled/{doctorId}")
    public String showFormForBookedAppointmentSlotsToDoctor(@PathVariable("doctorId") Long doctorId,
                                                            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                       Model model){
        List<Appointment> appointmentList = appointmentService.collectBookedSlots(doctorId,date);
        System.out.println("\n\nAppointment List count : \t" + appointmentList.size());
//        for(Appointment appointment:appointmentList){
//            System.out.println("\n\n\n\n appointment : \t\t\t"+ appointment.getId());
//        }
        Doctor doctor = doctorService.findById(doctorId).get();
        model.addAttribute("doctor",doctor );
        model.addAttribute("doctorBase64Image", Base64.getEncoder().encodeToString(doctor.getImage()));
        model.addAttribute("date", date);

        //Hard cored slots schedule here
        List<String> morningSlots = List.of("10:00","10:15","10:30","10:45","11:00");
        List<String> eveningSlots = List.of("06:00","06:15","06:30","06:45",
                "07:00","07:15","07:30","07:45","08:00");

        model.addAttribute("morningSlots",morningSlots);
        model.addAttribute("eveningSlots",eveningSlots);
        // To check appointment formatted time
        //System.out.println("\n\n\nBefore formatting appointment time  :\t" + appointmentList.get(1).getAppointmentTime());
        List<String> formattedAppointments = appointmentList.stream()
                .map(appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        model.addAttribute("appointments",formattedAppointments);
//        model.addAttribute("appointments",appointmentList.stream().map(appointment -> appointment.getAppointmentTime().toString()).toList());

        return "appointment/bookedAppointment";
    }
    @GetMapping("/goToConsultationRoom/{doctorId}")
    public String showConsultationRoomToDoctorForBookedAppointmentSlots(@RequestParam("doctorId") Long doctorId,
                                                            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                   Model model) {
    return "appointment/consultationRoom";
    }
}
