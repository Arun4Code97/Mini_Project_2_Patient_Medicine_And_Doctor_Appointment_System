package com.healthcare.doctor_consultation_medicine.Service.Implementation;

import com.healthcare.doctor_consultation_medicine.DTO.DoctorDto;
import com.healthcare.doctor_consultation_medicine.Mapper.DoctorMapper;
import com.healthcare.doctor_consultation_medicine.Model.Doctor;
import com.healthcare.doctor_consultation_medicine.Repository.DoctorRepository;
import com.healthcare.doctor_consultation_medicine.Service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final DoctorRepository doctorRepository;
    @Override
    public DoctorDto saveDoctorWithImage(DoctorDto doctorDto, MultipartFile imageFile) throws IOException {
        Doctor doctor = DoctorMapper.mapToEntity(doctorDto);
        if(!imageFile.isEmpty())
            doctor.setImage(imageFile.getBytes());
        return DoctorMapper.mapToDto(doctorRepository.save(doctor));
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
    public DoctorDto getSingleDoctor(Long id) {
        Optional<Doctor> retrivedDoctor = doctorRepository.findById(id);
        return DoctorMapper.mapToDto(retrivedDoctor.get());
    }

    @Override
    public List<DoctorDto> getAllDoctors() {
        List<Doctor> doctorList = doctorRepository.findAll();
        return doctorList.stream().map(DoctorMapper::mapToDto).toList();
    }

//    @Override
//    public DoctorDto updateDoctorById(Long id, DoctorDto doctorDto) {
//        Doctor doctor = DoctorMapper.mapToEntity(doctorDto);
//        return DoctorMapper.mapToDto(doctorRepository.save(doctor));
//    }

    @Override
    public void deletePatientById(Long id) {
        doctorRepository.deleteById(id);
    }
    @Override
    public Optional<DoctorDto> findDoctorByEmailId(String email){
        Optional<Doctor> doctor = doctorRepository.findOneByEmail(email);
        return doctor.map(DoctorMapper::mapToDto);
    }

    @Override
    public List<DoctorDto> getDoctorsBySpecialization() {
        return null;
    }
}
