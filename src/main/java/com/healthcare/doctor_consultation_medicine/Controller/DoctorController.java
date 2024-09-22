package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
    @Autowired
    private MedicineService medicineService;
    @GetMapping("/add")
    public String handlerMethodToShowForm(Model model){
        DoctorDto doctor = new DoctorDto();
        model.addAttribute("doctor",doctor);
        model.addAttribute("mode","add");
        return "doctor/showForm";
    }
    @PostMapping("/add")
    public String handlerMethodToAddDoctor(@Valid @ModelAttribute("doctor") DoctorDto newDoctor,
                                           BindingResult result,
                                           @RequestParam("imageFile") MultipartFile imageFile,
                                           Model model) throws IOException {
        model.addAttribute("mode","add");
        if(result.hasErrors())
            return "doctor/showForm";

        if(doctorService.isExistByEmail(newDoctor.getEmail())){
            model.addAttribute("error","Doctor Email ID "+ newDoctor.getEmail() + " exist already");
            return "doctor/showForm";}

        DoctorDto savedDoctor = doctorService.saveDoctorWithImage(newDoctor,imageFile);
        model.addAttribute("savedDoctor",savedDoctor);
        return "HttpResponse";
    }
    @GetMapping("/get/{id}")
    public String handlerMethodToGetSingleDoctor(@PathVariable Long id,Model model){
        model.addAttribute("doctor",new DoctorDto());
        if(doctorService.isExistById(id)){
        DoctorDto doctor = doctorService.getSingleDoctor(id);
        model.addAttribute("doctor",doctor);
        }else{
            model.addAttribute("error"," Doctor Id does not exist");
        }
        model.addAttribute("mode","view");
        return "doctor/showForm";
    }
    @GetMapping("/getAll")
    public String handlerMethodToGetAllDoctors(Model model){
        List<DoctorDto> doctorList = doctorService.getAllDoctors();
        if(doctorList.isEmpty())
            model.addAttribute("error","Database is empty");
        // Convert each doctor's image to Base64 and pass to the model
        List<Map<String, Object>> doctorDataList = new ArrayList<>();
        for (DoctorDto doctor : doctorList) {
            Map<String, Object> doctorData = new HashMap<>();
            doctorData.put("doctor", doctor);
            if (doctor.getImage() != null) {
                // Convert the byte[] to a Base64 encoded string
                String base64Image = Base64.getEncoder().encodeToString(doctor.getImage());
                doctorData.put("base64Image", base64Image);
            }
            doctorDataList.add(doctorData);
        }
        model.addAttribute("doctorDataList", doctorDataList);
        return "doctor/doctorList";
    }
    @GetMapping("/update/{id}")
    public String handlerMethodToShowForm(@PathVariable("id") Long id, Model model){
        model.addAttribute("doctor",new DoctorDto());
        if(doctorService.isExistById(id))
        {
            DoctorDto doctor = doctorService.getSingleDoctor(id);
            model.addAttribute("doctor",doctor);
        } else{
            model.addAttribute("error"," Doctor Id "+id+" does not exist");
        }
        model.addAttribute("mode","update");
        return "doctor/showForm";
    }
    @PutMapping("/update/{id}")
    public String handlerMethodForUpdateDoctor(@ModelAttribute("id") Long id,
                                               @Valid @ModelAttribute("doctor") DoctorDto doctorDto,
                                               BindingResult result,
                                               @RequestParam("imageFile") MultipartFile imageFile,
                                               Model model) throws IOException{
        model.addAttribute("mode","update");

        if(result.hasErrors()) {
            return "doctor/showForm";
        }

        DoctorDto updatedDoctor = doctorService.saveDoctorWithImage(doctorDto,imageFile );

        model.addAttribute("doctor",updatedDoctor);
        return "HttpResponse";

//        return "redirect:/doctor/getAll";
    }
    @GetMapping("delete/{id}")
    public String handlerMethodToConfirmDelete(@PathVariable("id") Long id,Model model ){
        model.addAttribute("mode","delete");
        model.addAttribute("id",id);
        return "doctor/confirmMessageBox";
    }
@DeleteMapping("delete/{id}")
    public String handlerMethodToDeleteDoctor(@PathVariable("id") Long id,Model model){
    model.addAttribute("mode", "delete");

    boolean isExist = doctorService.isExistById(id);
    if (isExist) {
      doctorService.deletePatientById(id);
    }
    else{
        model.addAttribute("error", "Patient ID " + id + " does not exist");
        return "httpResponse";
    }

    return "httpResponse"; // Render the response page
}

//-------------------------------------------------------------------------------//
//    For Medicine mapping
//-------------------------------------------------------------------------------//


    // Display form to add medicine
    @GetMapping("/{doctorId}/addMedicine/{patientId}")
    public String showAddMedicineForm(@PathVariable("doctorId") Long doctorId, @PathVariable("patientId") Long patientId, Model model) {
        model.addAttribute("medicine", new MedicineDto());
        model.addAttribute("doctorId", doctorId);
        model.addAttribute("patientId", patientId);
        return "doctor-medicine/addMedicine";
    }

    // Save medicine to the database
    @PostMapping("/{doctorId}/addMedicine/{patientId}")
    public String addMedicine(@PathVariable("doctorId") Long doctorId,
                              @PathVariable("patientId") Long patientId,
                              @ModelAttribute("medicine") MedicineDto medicine) {
        medicineService.addMedicine(doctorId, patientId,medicine);
//        System.out.println("\n\n\nAdded Medicines\t" + "Med-Id : " +medicine.getId()+ "Med-name : "+medicine.getName() +"Med-Dosage : "+ medicine.getDosage() +
//                "med-Duration : "+medicine.getDuration() + "Med-PrescribedDoctorId : "+ medicine.getDoctor().getId() + "Med-ToPatientId : " +medicine.getPatient().getId());
        return "redirect:/doctor/" + doctorId + "/medicines";
    }

    // Display form to update medicine
    @GetMapping("/{doctorId}/updateMedicine/{medicineId}")
    public String showUpdateMedicineForm(@PathVariable Long doctorId, @PathVariable Long medicineId, Model model) {
        MedicineDto medicine = medicineService.getSingleMedicineById(medicineId);
        model.addAttribute("medicine", medicine);
        model.addAttribute("doctorId", doctorId);
        return "doctor-medicine/updateMedicine";
    }

    // Update the medicine in the database
    @PostMapping("/{doctorId}/updateMedicine/{medicineId}")
    public String updateMedicine(@PathVariable Long doctorId, @PathVariable Long medicineId,
                                 @ModelAttribute("medicine") MedicineDto updatedMedicine) {
        medicineService.updateMedicine(medicineId, updatedMedicine);
        return "redirect:/doctor/" + doctorId + "/medicines";
    }

    // Delete medicine
    @GetMapping("/{doctorId}/deleteMedicine/{medicineId}")
    public String deleteMedicine(@PathVariable Long doctorId, @PathVariable Long medicineId) {
        medicineService.deleteMedicineById(medicineId);
        return "redirect:/doctor/" + doctorId + "/medicines";
    }

    // List all medicines for the doctor
    @GetMapping("/{doctorId}/medicines")
    public String viewDoctorMedicines(@PathVariable Long doctorId, Model model) {
        DoctorDto doctor = doctorService.getSingleDoctor(doctorId);
        model.addAttribute("medicines", doctor.getMedicines());
        return "viewDoctorMedicines";
    }
}
