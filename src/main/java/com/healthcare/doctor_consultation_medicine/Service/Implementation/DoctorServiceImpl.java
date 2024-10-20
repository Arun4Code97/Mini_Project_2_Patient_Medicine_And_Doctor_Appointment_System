package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Mapper.DoctorMapper;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Repository.AppointmentRepository;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Repository.MedicineRepository;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicineRepository medicineRepository;
    @Override
    public DoctorDto saveDoctorWithImage(DoctorDto doctorDto) {

        Doctor doctor = DoctorMapper.mapToEntity(doctorDto);

        return DoctorMapper.mapToDto(doctorRepository.save(doctor));
    }

    @Override
    public void setPassword(Long doctorId, String confirmPassword) {
        doctorRepository.findById(doctorId).ifPresent(
                doctor -> {
                    doctor.setPassword(confirmPassword);
                    doctorRepository.save(doctor);
                } );
    }
    @Override
    public boolean isExistByEmail(String email) {
        Optional<Doctor> doctor = doctorRepository.findOneByEmail(email);
        return doctor.isPresent();
    }

    @Override
    public boolean isExistById(Long id) {
        Optional<Doctor> retrivedDoctor = doctorRepository.findById(id);
        return retrivedDoctor.isPresent();
    }

    @Override
    public DoctorDto getSingleDoctorById(Long id) {
        Optional<Doctor> retrivedDoctor = doctorRepository.findById(id);
        return retrivedDoctor.map(DoctorMapper::mapToDto).orElse(null);
    }
    @Override
    public Optional<DoctorDto> findDoctorByEmailId(String email){
        Optional<Doctor> doctor = doctorRepository.findOneByEmail(email);
        return doctor.map(DoctorMapper::mapToDto);
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        List<Doctor> doctorList = doctorRepository.findAll();
        return doctorList.stream().map(DoctorMapper::mapToDto).toList();
    }

    @Transactional
    public void deleteDoctorById(Long id){
        if(doctorRepository.existsById(id)){
            appointmentRepository.deleteByDoctorId(id);
            medicineRepository.deleteByDoctorId(id);
            doctorRepository.deleteById(id);
        }
    }
}
