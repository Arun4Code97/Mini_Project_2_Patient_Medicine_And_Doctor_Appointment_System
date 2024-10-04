package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.DTO.PatientDto;
import com.healthcare.doctor_consultation_medicine.Others.CredentialDto;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import com.healthcare.doctor_consultation_medicine.Service.PatientService;
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
//@RequestMapping("/doctor")
@RequestMapping("/hospital")
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
        DoctorDto doctor = doctorService.getSingleDoctorById(id);
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
            DoctorDto doctor = doctorService.getSingleDoctorById(id);
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
        DoctorDto doctor = doctorService.getSingleDoctorById(doctorId);
        model.addAttribute("medicines", doctor.getMedicines());
        return "viewDoctorMedicines";
    }


//    ---------------------For DoctorPortal---------------------------------
    @GetMapping("/addDoctor")
    public String toHandleAddDoctorRequest(Model model) {
        DoctorDto doctor = new DoctorDto();
        model.addAttribute("doctor",doctor);
        model.addAttribute("mode","add");
        model.addAttribute("showDoctorForm","fragmentName");
//        model.addAttribute("doctorBase64Image", null);
//        model.addAttribute("showDoctorForm");
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
//        DoctorDto savedDoctor = doctorService.saveDoctor(doctor);
        DoctorDto savedDoctor = doctorService.saveDoctorWithImage(doctor,imageFile);

        model.addAttribute("setPassword","fragmentName");

        return "redirect:/hospital/addDoctor/setPassword?savedDoctorId=" + savedDoctor.getId();
    }

    @GetMapping("/addDoctor/setPassword")
    public String toHandleSetPasswordRequest( @RequestParam("savedDoctorId") Long doctorId,
                                              Model model){
//        System.out.println("\n\n\n From get Mapping -> Request Param :\t " + doctorId);
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
        // For JavaScript added notification and redirect to Home page
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
        model.addAttribute("doctor",doctor);
        model.addAttribute("mode", "update");
        model.addAttribute("showDoctorForm", "fragmentName"); // Switch to update fragment
        if(doctor.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(doctor.getImage());
            model.addAttribute("doctorBase64Image",base64Image);
        }
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
        doctorService.saveDoctorWithImage(doctorDto,imageFile);

        return "redirect:/hospital/doctorPortal/"+ id;
    }
    @GetMapping("/doctorPortal/deleteDoctor/{id}")
    public String toHandleDeleteDoctorRequest(@PathVariable("id") Long id,Model model){
        model.addAttribute("mode","delete");
        model.addAttribute("deleteDoctor", "fragmentName");
        model.addAttribute("doctor",doctorService.getSingleDoctorById(id));
        return "doctor/doctorPortal";
    }
    @DeleteMapping("/doctorPortal/deleteDoctor/{id}")
    public String toHandleDeleteDoctor(@PathVariable("id") Long id){
        doctorService.deleteDoctorById(id);
        return "redirect:/hospital";
    }
}
