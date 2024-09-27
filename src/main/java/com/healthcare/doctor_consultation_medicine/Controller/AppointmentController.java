package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Model.Appointment;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Service.AppointmentService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import com.healthcare.doctor_consultation_medicine.common.AppointmentSlots;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    private final DoctorRepository doctorService;
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

        Doctor doctor = doctorService.findById(doctorId).get();

        List<String> formattedAppointments = bookedAppointmentList.stream()
                .map(appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                .collect(Collectors.toList());

        model.addAttribute("appointments",formattedAppointments);
        model.addAttribute("doctor",doctor );
        model.addAttribute("doctorBase64Image", Base64.getEncoder().encodeToString(doctor.getImage()));
        model.addAttribute("patient", patientService.getPatientById(patientId).get());
        model.addAttribute("date", date);
        model.addAttribute("morningSlots", AppointmentSlots.morningSlots);
        model.addAttribute("eveningSlots", AppointmentSlots.eveningSlots);
        model.addAttribute("scheduleAppointment","fragmentName");
        return "patient/patientPortal";
//        return "appointment/scheduleAppointment";
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
        model.addAttribute("doctor",appointment.getDoctor());
        model.addAttribute("scheduleAppointmentConfirmation","fragmentName");

        return "patient/patientPortal";
        //        return "appointment/confirmationPage";
   }
    // Here doctor can click on show patient records Button only on already booked slots
    @GetMapping("/scheduled/{doctorId}")
    public String showFormForBookedAppointmentSlotsToDoctor(@PathVariable("doctorId") Long doctorId,
                                                            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                       Model model){
        List<Appointment> appointmentList = appointmentService.collectBookedSlots(doctorId,date);
//        System.out.println("\n\nAppointment List count : \t" + appointmentList.size());
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
        System.out.println("\n\n\nAfter formatting appointment time  :\t" + appointmentList.get(1).getAppointmentTime());

        model.addAttribute("appointments",formattedAppointments);
//        model.addAttribute("appointments",appointmentList.stream().map(appointment -> appointment.getAppointmentTime().toString()).toList());

        return "appointment/bookedAppointment";
    }
    @GetMapping("/goToConsultationRoom")
    public String showConsultationRoomToDoctorForBookedAppointmentSlots(@RequestParam("doctorId") Long doctorId,
                                                                        @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
                                                                        @RequestParam(name="time") String timeStr,
                                                   Model model) {
        System.out.println("Before Formatting time as String : \t" + timeStr);
        // Manually parse the time string into LocalTime
        LocalTime time = LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
        System.out.println("After Formatting time as LocalTime : \t" + timeStr);
        PatientDto patient =appointmentService.fetchPatientRecord(doctorId,date,time);

        model.addAttribute("mode","view");
        List<MedicineDto> medicineList = new ArrayList<>();

//        System.out.println("Before - Retrieved Medicine List: " + 0);

        medicineList = medicineService.getAllMedicineByPatientId(Long.parseLong(patient.getId()));

//       System.out.println("\n\nAfter fetching medicines from medicineRepo using query : \n\n" + medicineList.get(1).toString());
//        System.out.println("After - Retrieved Medicine properly : \t" + medicineList.size());
//        patient.setMedicines(null);
        patient.setMedicines(medicineList);

        for(MedicineDto item:medicineList){
            System.out.println(item);
        }
//        System.out.println("\n\n\nDisplay Details with list member field :\t"+patient.getMedicines());
        model.addAttribute("patient",patient);
        model.addAttribute("doctorId",doctorId);

        return "patient/patientComponent";
    }
}
//@GetMapping("/schedule/{doctorId}/{patientId}")
//    public String showFormForAvailableAppointmentSlots(@PathVariable("doctorId") Long doctorId,
//                                                       @PathVariable("patientId") Long patientId,
//                                                       @RequestParam(value = "date",required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
//                                                       Model model){
//        if (date == null) {
//            date = LocalDate.now();
//        }
//        List<Appointment> appointmentList = appointmentService.collectBookedSlots(doctorId,date);
////        System.out.println("\n\nAppointment List count : \t" + appointmentList.size());
////        for(Appointment appointment:appointmentList){
////            System.out.println("\n\n\n\appointment : \t\t\t"+ appointment.getId());
////        }
//        Doctor doctor = doctorService.findById(doctorId).get();
//        model.addAttribute("doctor",doctor );
//        model.addAttribute("doctorBase64Image", Base64.getEncoder().encodeToString(doctor.getImage()));
//        model.addAttribute("patient", patientService.getPatientById(patientId).get());
//        model.addAttribute("date", date);
//
//        List<String> morningSlots = List.of("10:00","10:15","10:30","10:45","11:00");
//        List<String> eveningSlots = List.of("06:00","06:15","06:30","06:45",
//                "07:00","07:15","07:30","07:45","08:00");
//
//        model.addAttribute("morningSlots",morningSlots);
//        model.addAttribute("eveningSlots",eveningSlots);
//
//        List<String> formattedAppointments = appointmentList.stream()
//                .map(appointment -> appointment.getAppointmentTime().format(DateTimeFormatter.ofPattern("HH:mm")))
//                .collect(Collectors.toList());
//
//        model.addAttribute("appointments",formattedAppointments);
////        model.addAttribute("appointments",appointmentList.stream().map(appointment -> appointment.getAppointmentTime().toString()).toList());
//
//        return "appointment/scheduleAppointment";
//    }