package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.DTO.UserLoginDTO;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HomeController {
    private final PatientService patientService;
    private final DoctorService doctorService;
    @GetMapping
    public String toHandleHomePageRequest(Model model){
        UserLoginDTO userData = new UserLoginDTO();
        userData.setUserType("patient"); // Set default value to patient
        model.addAttribute("userData",userData);
        return "common/homePage";
    }
    @PostMapping("/login")
    public String toHandleLoginRequest(@Valid @ModelAttribute("userData") UserLoginDTO userData,
                                       BindingResult result, Model model) {
        // If validation errors exist, return to home page
        if (result.hasErrors()) {
            return "common/homePage";
        }
        // Handle login based on user type
        if ("patient".equals(userData.getUserType())) {
            return handlePatientLogin(userData, model);
        } else if ("doctor".equals(userData.getUserType())) {
            return handleDoctorLogin(userData, model);
        } else {
            model.addAttribute("error", "Invalid user type.");
            return "common/homePage";
        }
    }

    // Handle patient login
    private String handlePatientLogin(UserLoginDTO userData, Model model) {
        Optional<PatientDto> existPatient = patientService.findPatientByEmailId(userData.getEmail());

        if (existPatient.isPresent()) {
            if (existPatient.get().getPassword().equals(userData.getPassword())) {
                return "redirect:/hospital/patientPortal/" + existPatient.get().getId();
            } else {
                model.addAttribute("error", "Incorrect password.");
            }
        } else {
            model.addAttribute("error", "Email ID " + userData.getEmail() + " does not exist.");
        }
        return "common/homePage";
    }

    // Handle doctor login
    private String handleDoctorLogin(UserLoginDTO userData, Model model) {
        Optional<DoctorDto> existDoctor = doctorService.findDoctorByEmailId(userData.getEmail());

        if (existDoctor.isPresent()) {
            if (existDoctor.get().getPassword().equals(userData.getPassword())) {
                return "redirect:/hospital/doctorPortal/" + existDoctor.get().getId();
            } else {
                model.addAttribute("error", "Incorrect password.");
            }
        } else {
            model.addAttribute("error", "Email ID " + userData.getEmail() + " does not exist.");
        }
        return "common/homePage";
    }
}
