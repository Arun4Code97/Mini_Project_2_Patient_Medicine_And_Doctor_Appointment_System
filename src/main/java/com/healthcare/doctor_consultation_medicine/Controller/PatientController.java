package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Service.Implementation.PatientServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patient")
public class PatientController {
    private final PatientServiceImpl patientService;
    @GetMapping(value = "/getAll" )
    public String handlerMethodToGetAllPatient(Model model){
        List<PatientDto> patientList = patientService.getAllPatient();
        model.addAttribute("patientList",patientList);
        return "patient/patientList";
    }
    @GetMapping("get/{id}")
    public String handlerMethodForGetSinglePatient(@PathVariable Long id,Model model){
        Optional<PatientDto> patient = patientService.getPatientById(id);
        model.addAttribute("patient",new PatientDto());

        patient.ifPresentOrElse(
                patientDto  -> model.addAttribute("patient",patientDto),
                ()          -> model.addAttribute("error","Patient ID "+ id + " does not exist")
                        );
        model.addAttribute("mode","view");
        return "patient/patientComponent";
    }
    @GetMapping("/add")
    public String handlerMethodToShowForm(Model model)
    {
        PatientDto newPatient = new PatientDto();
        model.addAttribute("patient",newPatient);
        model.addAttribute("mode","add");
        return "patient/patientComponent";
    }
    @PostMapping("/add")
    public String handlerMethodForAddPatient(@Valid @ModelAttribute("patient") PatientDto patientDto, BindingResult result,Model model){

        if(result.hasErrors()) {
            return "patient/patientComponent";
        }

        if( patientService.existByEmailId(patientDto.getEmail()) ){
            model.addAttribute("error","Patient Email ID "+ patientDto.getEmail() + " exist already");
            return "patient/patientComponent";
        }

        PatientDto patient = patientService.addPatient(patientDto);

//        model.addAttribute("savedPatient",patient);

        model.addAttribute("mode","add");
        return "HttpResponse";
    }
    @GetMapping("/update/{id}")
    public String handlerMethodForUpdateForm (@PathVariable Long id,Model model)
    {
        model.addAttribute("patient", new PatientDto());

        Optional<PatientDto> retrivedPatient = patientService.getPatientById(id);

        retrivedPatient.ifPresentOrElse(
                patientDto -> model.addAttribute("patient", patientDto),
                () -> model.addAttribute("error","Patient ID "+ id + " does not exist")
                );

//        System.out.println("\n\n\n\nFrom Update @GetMapping -> retrived patient details : " + model.getAttribute("patient")+"\n\n\n\n");

        model.addAttribute("id",id);
        model.addAttribute("mode","update");
        System.out.println("\n\n\n\n from get-update"+ id);
        return "patient/patientComponent";
    }
    @PutMapping("/update/{id}")
    public String handlerMethodForUpdatePatient(@ModelAttribute("id") Long id, @Valid @ModelAttribute("patient") PatientDto patientDto,BindingResult result,Model model){
        System.out.println("\n\n\n\n from put-update"+ id);
        if(result.hasErrors()) {
            return "patient/patientComponent";
        }
        PatientDto updatedPatient = patientService.updatePatientById(id,patientDto);

        if (updatedPatient == null)
            model.addAttribute("error", "Patient ID " + id + " does not exist");

//        System.out.println("\n\n\n\nFrom Update @PutMapping -> After update - The patient details : " + updatedPatient +"\n\n\n\n");
        model.addAttribute("updatedPatient",updatedPatient);
        model.addAttribute("mode","update");

        return "HttpResponse";
    }
    @GetMapping("/delete/{id}")
    public String handlerMethodForDeleteRequest(@PathVariable Long id,Model model)
    {
    model.addAttribute("id",id);
    return "patient/deletePatient";
    }
    @DeleteMapping("/delete/{id}")
    public String handlerMethodForDeletePatient(@PathVariable Long id, Model model) {
        boolean isDeleted = patientService.deletePatientById(id);
        if (!isDeleted) {
            model.addAttribute("error", "Patient ID " + id + " does not exist");
        }
        model.addAttribute("mode", "delete");
        return "httpResponse"; // Render the response page
    }
//---------------------------------------------------------------------------------
    // View prescribed medicines for a patient
//-------------------------------------------------------------------------------
    @GetMapping("/{id}/medicines")
    public String viewPatientMedicines(@PathVariable Long id, Model model) {
        model.addAttribute("patient", new PatientDto());
        List<MedicineDto> medicines = new ArrayList<>();

        Optional<PatientDto> patient = patientService.getPatientById(id);
        if(patient.isPresent()) {
           medicines = patient.get().getMedicines(); // Get medicines from the patient
        }
        model.addAttribute("patient", patient);
        model.addAttribute("medicines", medicines);
        return "viewPatientMedicines"; // Thymeleaf template for viewing medicines
    }

}

