package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Others.CredentialDto;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
//    ---------------------For DoctorPortal---------------------------------
@Controller
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/addDoctor")
    public String toHandleAddDoctorRequest(Model model) {
        DoctorDto doctor = new DoctorDto();
        model.addAttribute("doctor",doctor);
        model.addAttribute("mode","add");
        model.addAttribute("showDoctorForm","fragmentName");
        return "doctor/addDoctor";
    }
    @PostMapping("/addDoctor")
    public String toHandleAddDoctor(@Valid @ModelAttribute("doctor") DoctorDto doctor,
                                    BindingResult result,
                                    @RequestParam("imageFile") MultipartFile imageFile,
                                    Model model) throws IOException {

        model.addAttribute("mode","add");

        if(result.hasErrors()){
            model.addAttribute("showDoctorForm","fragmentName");
            return "doctor/addDoctor";
        }
        //Handle Error if mail ID already exist
        if( doctorService.isExistByEmail(doctor.getEmail()) ){
            model.addAttribute("showDoctorForm","fragmentName");
            model.addAttribute("errorExistEmail","Doctor Email ID already exist");
            return "doctor/addDoctor";
        }

         if( !imageFile.isEmpty() ){
            doctor.setImage(imageFile.getBytes());
            }

        DoctorDto savedDoctor = doctorService.saveDoctorWithImage(doctor);
        model.addAttribute("setPassword","fragmentName");

        return "redirect:/hospital/addDoctor/setPassword?savedDoctorId=" + savedDoctor.getId();
    }

    @GetMapping("/addDoctor/setPassword")
    public String toHandleSetPasswordRequest( @RequestParam("savedDoctorId") Long doctorId,
                                              Model model){
        model.addAttribute("mode","add");
        model.addAttribute("credentials",new CredentialDto());
        model.addAttribute("setPassword", "fragmentName");
        model.addAttribute("savedDoctorId",doctorId);
        return "doctor/addDoctor";
    }
    @PostMapping("/addDoctor/setPassword")
    public String toHandleSetPassword(   @Valid @ModelAttribute("credentials") CredentialDto credentials,
                                         BindingResult result,
                                         @RequestParam("savedDoctorId") Long doctorId,
                                         Model model) throws IOException {

        model.addAttribute("mode","add");
        model.addAttribute("savedDoctorId",doctorId);

        if(result.hasErrors()){
            model.addAttribute("setPassword","fragmentName");
            return "doctor/addDoctor";
        }
        if( ! credentials.getPassword().equals( credentials.getConfirmPassword() ) ) {
            model.addAttribute("setPassword","fragmentName");
            model.addAttribute("error","Passwords are not matching");
            return "doctor/addDoctor";
        }
        doctorService.setPassword(doctorId, credentials.getConfirmPassword());
        // For showing the success notification and redirect to respective doctor portal home page
        model.addAttribute("success", true);
        model.addAttribute("savedDoctorId",doctorId);
        return "doctor/addDoctor";
    }

    @GetMapping("/doctorPortal/{id}")
    public String toHandleDoctorViewRequest(@PathVariable("id") Long doctorId,Model model){

        DoctorDto doctor = doctorService.getSingleDoctorById(doctorId);
        model.addAttribute("doctor",doctor);
        model.addAttribute("mode","view");
        model.addAttribute("showDoctorForm","fragmentName");
        if(doctor.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(doctor.getImage());
            model.addAttribute("doctorBase64Image",base64Image);
        }
        return "doctor/doctorPortal";
    }

    @GetMapping("/doctorPortal/updateProfile/{id}")
    public String toHandleUpdateProfileRequest(@PathVariable("id") Long doctorId, Model model) {
        DoctorDto doctor = doctorService.getSingleDoctorById(doctorId);
        if(doctor != null){
            model.addAttribute("doctor",doctor);
            model.addAttribute("mode", "update");
            model.addAttribute("showDoctorForm", "fragmentName"); // Switch to update fragment
            if(doctor.getImage() != null) {
                String base64Image = Base64.getEncoder().encodeToString(doctor.getImage());
                model.addAttribute("doctorBase64Image",base64Image);
            }
        }
        else
            model.addAttribute("error"," Doctor Id "+doctorId+" does not exist");
        return "doctor/doctorPortal";
    }
    @PutMapping("/doctorPortal/updateProfile/{id}")
    public String toHandleUpdateProfile(@PathVariable("id") Long id,
                                        @Valid @ModelAttribute("doctor") DoctorDto doctorDto,
                                        BindingResult result,
                                        @RequestParam("imageFile") MultipartFile imageFile,
                                        Model model) throws IOException{

        if(result.hasErrors()){
            model.addAttribute("mode","update");
            model.addAttribute("showDoctorForm", "fragmentName");
            return "doctor/doctorPortal";
        }
        if( !imageFile.isEmpty() ){
            doctorDto.setImage(imageFile.getBytes());
        }

        doctorService.saveDoctorWithImage(doctorDto);
        return "redirect:/hospital/doctorPortal/"+ id;
    }
    @GetMapping("/doctorPortal/deleteDoctor/{id}")
    public String toHandleDeleteDoctorRequest(@PathVariable("id") Long id,Model model){
        model.addAttribute("mode","delete");
        model.addAttribute("deleteDoctor", "fragmentName");
        if(doctorService.isExistById(id))
            model.addAttribute("doctor",doctorService.getSingleDoctorById(id));
        else model.addAttribute("error"," Doctor Id "+id+" does not exist");
        return "doctor/doctorPortal";
    }
    @DeleteMapping("/doctorPortal/deleteDoctor/{id}")
    public String toHandleDeleteDoctor(@PathVariable("id") Long id){
            doctorService.deleteDoctorById(id);
        return "redirect:/hospital";
    }
}
