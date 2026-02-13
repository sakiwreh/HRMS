package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.JobOpeningRequestDto;
import com.capestone.hrms_backend.dto.response.JobOpeningResponseDto;
import com.capestone.hrms_backend.entity.job.JobOpening;
import com.capestone.hrms_backend.entity.job.JobStatus;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.job.JobOpeningRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IJobOpeningService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobOpeningService implements IJobOpeningService {
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileService;
    private final JobOpeningRepository jobOpeningRepository;
    private final ModelMapper modelMapper;

    @Override
    public JobOpeningResponseDto create(JobOpeningRequestDto dto) throws IOException {
        Employee employee = employeeRepository.findById(dto.getHrId()).orElseThrow(()->new ResourceNotFoundException("Employee doesn't exist."));
        JobOpening opening = new JobOpening();
        opening.setCreatedBy(employee);
        opening.setDescription(dto.getDescription());
        opening.setTitle(dto.getTitle());
        opening.setCommunicationEmail(dto.getCommunicationEmail());
        opening.setExperienceRequired(dto.getExperienceRequired());
        opening.setCommunicationEmail(dto.getCommunicationEmail());

        String path = fileService.saveJd(dto.getTitle(), dto.getFile());
        opening.setJobDescriptionUrl(path);

        jobOpeningRepository.save(opening);
        return modelMapper.map(opening, JobOpeningResponseDto.class);
    }

    @Override
    public JobOpeningResponseDto getById(Long id) {
        JobOpening opening = jobOpeningRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Opening doesn't exist"));
        return modelMapper.map(opening, JobOpeningResponseDto.class);
    }

    @Override
    public List<JobOpeningResponseDto> getAllOpenings() {
        return jobOpeningRepository.findAll().stream().map(opening -> modelMapper.map(opening, JobOpeningResponseDto.class)).toList();
    }

    @Override
    public void changeStatus(Long id, JobStatus status, String remarks) {
        JobOpening opening = jobOpeningRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Job opening doesn't exist"));
        opening.setStatus(status);
        opening.setReason(remarks);
        jobOpeningRepository.save(opening);
    }
}
