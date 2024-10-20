package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Others.CredentialDto;
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
@RequiredArgsConstructor
@RequestMapping("/hospital")
public class PatientController {
    private final PatientService patientService;
    private final DoctorService doctorService;

    @GetMapping("/addPatient")
    public String toHandleAddPatientRequest(Model model){
        PatientDto patient = new PatientDto();
        model.addAttribute("patient",patient);
        model.addAttribute("mode","add");
        model.addAttribute("showPatientForm","fragmentName");
        return "patient/addPatient";
    }
    @PostMapping("/addPatient")
    public String toHandleAddPatient(@Valid @ModelAttribute("patient") PatientDto patient,
                                     BindingResult result, Model model){

        model.addAttribute("mode","add");
        if(result.hasErrors()){
            model.addAttribute("showPatientForm","fragmentName");
            return "patient/addPatient";
        }
        //Handle Error if mail ID already exist
        if( patientService.existByEmailId(patient.getEmail()) ){
            model.addAttribute("showPatientForm","fragmentName");
            model.addAttribute("errorExistEmail","Patient Email ID already exist");
            return "patient/addPatient";
        }
        PatientDto savedPatient = patientService.addPatient(patient);
        model.addAttribute("setPassword","fragmentName");
        return "redirect:/hospital/addPatient/setPassword?savedPatientId=" + savedPatient.getId();
    }

    @GetMapping("/addPatient/setPassword")
    public String toHandleSetPasswordRequest( @RequestParam("savedPatientId") Long patientId,
                                                          Model model){
        model.addAttribute("mode","add");
        model.addAttribute("credentials",new CredentialDto());
        model.addAttribute("setPassword", "fragmentName");
        model.addAttribute("savedPatientId",patientId);
        return "patient/addPatient";
    }

    @PostMapping("/addPatient/setPassword")
    public String toHandleSetPassword(   @Valid @ModelAttribute("credentials") CredentialDto credentials,
                                         BindingResult result,
                                         @RequestParam("savedPatientId") Long patientId,
                                         Model model){
        model.addAttribute("mode","add");
        model.addAttribute("savedPatientId",patientId);
        if(result.hasErrors()){
            model.addAttribute("setPassword","fragmentName");
            return "patient/addPatient";
        }
        if( ! credentials.getPassword().equals( credentials.getConfirmPassword() ) ) {
            model.addAttribute("setPassword","fragmentName");
            model.addAttribute("error","Passwords are not matching");
            return "patient/addPatient";
        }
        patientService.setPassword(patientId, credentials.getConfirmPassword());
        // For showing added notification and redirect to Doctor Home page
        model.addAttribute("success", true);
        return "patient/addPatient";
    }
    @GetMapping("/patientPortal/{id}")
    public String toHandlePatientViewProfileRequest(@PathVariable("id") Long patientId,Model model){
        PatientDto patient = patientService.getSinglePatientById(patientId);
        model.addAttribute("patient",patient);
        model.addAttribute("mode","view");
        model.addAttribute("showPatientForm","fragmentName");
        return "patient/patientPortal";
    }

    @GetMapping("/patientPortal/updateProfile/{id}")
    public String toHandleUpdateProfileRequest(@PathVariable("id") Long patientId, Model model) {
        PatientDto patient = patientService.getSinglePatientById(patientId);
        model.addAttribute("patient", patient);
        model.addAttribute("mode", "update");
        model.addAttribute("showPatientForm", "fragmentName"); // Switch to update fragment
        return "patient/patientPortal";
    }

    @PutMapping("/patientPortal/updateProfile/{id}")
    public String toHandleUpdateProfile(@PathVariable("id") Long id,
                                        @Valid @ModelAttribute("patient") PatientDto patientDto,
                                        BindingResult result,Model model){
        if(result.hasErrors()){
            model.addAttribute("mode","update");
            model.addAttribute("showPatientForm", "fragmentName");
            return "patient/patientPortal";
        }
        patientService.updatePatientById(id,patientDto);
        model.addAttribute("mode","view");
        return "redirect:/hospital/patientPortal/"+ id;
    }

    @GetMapping("/patientPortal/deletePatient/{id}")
    public String toHandleDeletePatientRequest(@PathVariable("id") Long id,Model model){
        model.addAttribute("mode","delete");
        model.addAttribute("deletePatient", "fragmentName");
        model.addAttribute("patient",patientService.getSinglePatientById(id));
        return "patient/patientPortal";
    }

    @DeleteMapping("/patientPortal/deletePatient/{id}")
    public String toHandleDeletePatient(@PathVariable("id") Long id){
        patientService.deletePatientById(id);
        return "redirect:/hospital";
    }


    @GetMapping("/patientPortal/{id}/showDoctorsBySpecialization")
    public String toShowDoctorsBySpecialization(@PathVariable Long id , Model model){
        List<DoctorDto> doctorList= doctorService.getAllDoctors();
        if(doctorList.isEmpty())
            model.addAttribute("error","No doctors found in the database.");

        List<Map<String,Object>> doctorListWithConvertedImage = new ArrayList<>();
        for(DoctorDto doctor : doctorList){
            Map<String,Object> doctorData = new HashMap<>();
            doctorData.put("doctor",doctor);
            if (doctor.getImage() != null) {
                // Convert the byte[] to a Base64 encoded string
                String base64Image = Base64.getEncoder().encodeToString(doctor.getImage());
                doctorData.put("base64Image", base64Image);
            }
            doctorListWithConvertedImage.add(doctorData);
        }

        model.addAttribute("doctorsData",doctorListWithConvertedImage);
        model.addAttribute("mode","showDoctorsBySpecialization");
        model.addAttribute("showDoctors", "fragmentName");
        model.addAttribute("patient",patientService.getSinglePatientById(id));
        return "patient/patientPortal";
    }

}

