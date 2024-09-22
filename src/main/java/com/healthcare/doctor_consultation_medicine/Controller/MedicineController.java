package com.healthcare.doctor_consultation_medicine.Controller;

import com.healthcare.doctor_consultation_medicine.DTO.MedicineDto;
import com.healthcare.doctor_consultation_medicine.Model.Medicine;
import com.healthcare.doctor_consultation_medicine.Service.MedicineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MedicineController {
    private final MedicineService medicineService;


    @GetMapping(value = {"/medicine/form/{mode}", "/medicine/form/{mode}/{id}"})
    public String handlerMethodToShowForm(@PathVariable("mode") String mode,
                                          @PathVariable(value = "id", required = false) Long id,
                                          Model model) {
        MedicineDto medicine = (id != null) ? medicineService.getSingleMedicineById(id) : new MedicineDto();
        model.addAttribute("medicine", medicine);
        model.addAttribute("mode", mode);
        System.out.println("testing\n\n\n" + mode);

        String formAction;
        if (mode.equals("add")) {
            formAction = "/medicine/add";
        } else if (mode.equals("update")) {
            formAction = "/medicine/update/" + medicine.getId();
        } else
            formAction = "#";

        model.addAttribute("formAction", formAction);
        return "medicine/showForm";
    }

    @PostMapping("/medicine/add")
    public String handlerMethodToAddMedicine(@Valid @ModelAttribute("medicine") MedicineDto medicineDto, BindingResult result, Model model) {

        model.addAttribute("mode", "add");

        if (result.hasErrors())
            return "medicine/showForm";
        MedicineDto savedMedicine = medicineService.saveMedicine(medicineDto);
        System.out.println("From PostMapping\n\n\n\n" + savedMedicine + "\n\n\n");
        return "httpResponse";
    }

    @GetMapping("/medicine/list")
    public String handlerMethodForListAllMedicine(Model model) {
        List<MedicineDto> medicineList = medicineService.getAllMedicine();
        if (!medicineList.isEmpty()) {
            model.addAttribute("medicineList", medicineList);
        } else
            model.addAttribute("error", "No data found in database");

        return "medicine/medicineList";
    }

    @PutMapping("/medicine/update/{id}")
    public String handlerMethodToUpdateMedicine(@PathVariable("id") Long id, @Valid @ModelAttribute("medicine") MedicineDto medicineDto, BindingResult result, Model model) {

        model.addAttribute("mode", "update");

        if (result.hasErrors())
            return "medicine/showForm";

        MedicineDto savedMedicine = medicineService.saveMedicine(medicineDto);

        System.out.println("From update or PUT Mapping\n\n\n\n" + savedMedicine + "\n\n\n");
        return "httpResponse";
    }

    @DeleteMapping("/medicine/delete/{id}")
    public String handlerMethodToDeleteDoctor(@PathVariable("id") Long id, Model model) {
        model.addAttribute("mode", "delete");

        boolean isExist = medicineService.isExistById(id);
        if (isExist) {
            medicineService.deleteMedicineById(id);
        } else {
            model.addAttribute("error", "Patient ID " + id + " does not exist");
        }

        return "httpResponse"; // Render the response page
    }

//    @GetMapping("/medicine/get/{id}")
//    public String handlerMethodToGetSingleDoctor(@PathVariable("id") Long id,Model model){
//        model.addAttribute("mode", "view");
//
//        boolean isExist = medicineService.isExistById(id);
//        if (isExist) {
//            MedicineDto medicine =medicineService.getSingleMedicineById(id);
//            model.addAttribute("medicine",medicine);
//        }
//        else{
//            model.addAttribute("error", "Patient ID " + id + " does not exist");
//            return "httpResponse";
//        }
//
//        return "medicine/showForm";// Render the response page
//    } Looks like not required as we gonna display all medicine in doctor end with all edit option
//    patient side with only view options
}
