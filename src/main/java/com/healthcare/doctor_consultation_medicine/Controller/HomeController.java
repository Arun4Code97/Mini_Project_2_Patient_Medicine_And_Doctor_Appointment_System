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
    @GetMapping("")
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
                    return "redirect:/doctorPortal/" + existDoctor.get().getId() ;
                else
                    model.addAttribute("error","Incorrect password");
            }
            else{
                model.addAttribute("error","Email ID "+userData.getEmail() +" does not exist");
            }
        }
        return "common/homePage";
    }
    @GetMapping("/addPatient")
    public String toHandleAddPatientRequest(Model model){
        PatientDto patient = new PatientDto();
        model.addAttribute("patient",patient);
        model.addAttribute("mode","add");
        model.addAttribute("showPatientForm","fragmentName");
        return "patient/addPatient";
    }
    @PostMapping("/addPatient")
    public String toHandleAddPatient(@Valid @ModelAttribute("patient") PatientDto patient,BindingResult result, Model model){
        if(result.hasErrors())
            return "patient/addPatient";
        //Handle Error if mail ID already exist
        if( patientService.existByEmailId(patient.getEmail()) ){
            model.addAttribute("error","Patient Email ID "+ patient.getEmail() + " exist already");
            return "patient/addPatient";
        }
        patientService.addPatient(patient);
        //need to redirect for setting the password
        model.addAttribute("setPassword","fragmentName");
        return "patient/addPatient";
    }
    @GetMapping("/patientPortal/{id}")
    public String toHandlePatientViewRequest(@PathVariable("id") Long patientId,Model model){
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
    public String handlerMethodForUpdatePatient(@PathVariable("id") Long id, @Valid @ModelAttribute("patient") PatientDto patientDto,BindingResult result,Model model){

        model.addAttribute("mode","update");
        model.addAttribute("showPatientForm", "fragmentName");

        if(result.hasErrors())
            return "patient/patientPortal";

        patientService.updatePatientById(id,patientDto);

        return "redirect:/hospital/patientPortal/"+ id;
    }

    @GetMapping("/patientPortal/deletePatient/{id}")
    public String handlerMethodForDeletePatientRequest(@PathVariable("id") Long id,Model model){
        model.addAttribute("mode","delete");
        model.addAttribute("deletePatient", "fragmentName");
        model.addAttribute("patient",patientService.getSinglePatientById(id));
        return "patient/patientPortal";
    }

    @DeleteMapping("/patientPortal/deletePatient/{id}")
    public String handlerMethodForDeletePatient(@PathVariable("id") Long id){
        patientService.deletePatientById(id);
        return "redirect:/hospital";
    }

    @GetMapping("/patientPortal/{id}/medicines")
    public String handlerMethodForShowingMedicines(@PathVariable Long id ,Model model){
        model.addAttribute("mode","medicineView");
        model.addAttribute("medicineView", "fragmentName");
        model.addAttribute("patient",patientService.getSinglePatientById(id));
        return "patient/patientPortal";
    }

    @GetMapping("/patientPortal/{id}/showDoctorsBySpecialization")
    public String handlerMethodForShowDoctorsBySpecialization(@PathVariable Long id , Model model){
        List<DoctorDto> doctorList= doctorService.getAllDoctors();
        if(doctorList.isEmpty())
            model.addAttribute("error","Database is empty");

        List< Map<String,Object> > doctorListWithConvertedImage = new ArrayList<>();
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
