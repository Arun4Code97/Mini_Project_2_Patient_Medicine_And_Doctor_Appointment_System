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
    public String toHandleLoginRequest(@Valid @ModelAttribute("userData")UserLoginDTO userData, BindingResult result,Model model){
        if(result.hasErrors())
            return "common/homePage";

        if(userData.getUserType().equals("patient"))
        {
            Optional<PatientDto> existPatient = patientService.findPatientByEmailId(userData.getEmail());
            if(existPatient.isPresent())
            {
                if(existPatient.get().getPassword().equals(userData.getPassword()))
                    return "redirect:/hospital/patientPortal/" + existPatient.get().getId() ;
//                    return "redirect:/patient/get/" + existPatient.get().getId() ;
                else
                    model.addAttribute("error","Incorrect password");
            }
            else {
                model.addAttribute("error","Email ID "+userData.getEmail() +" does not exist");
            }
        }
        else if(userData.getUserType().equals("doctor"))
        {
            Optional<DoctorDto> existDoctor = doctorService.findDoctorByEmailId(userData.getEmail());
            if(existDoctor.isPresent() )
            {
                if(existDoctor.get().getPassword().equals(userData.getPassword()))
                    return "redirect:/hospital/doctorPortal/" + existDoctor.get().getId() ;
                else
                    model.addAttribute("error","Incorrect password");
            }
            else{
                model.addAttribute("error","Email ID "+userData.getEmail() +" does not exist");
            }
        }
        return "common/homePage";
    }


}
