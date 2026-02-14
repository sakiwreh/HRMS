package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.JobOpeningRequestDto;
import com.capestone.hrms_backend.dto.response.JobOpeningResponseDto;
import com.capestone.hrms_backend.dto.response.JobOpeningReviewerResponseDto;
import com.capestone.hrms_backend.entity.job.JobCvReviewer;
import com.capestone.hrms_backend.entity.job.JobOpening;
import com.capestone.hrms_backend.entity.job.JobStatus;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.TravelPlanParticipant;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.job.JobCvReviewerRepository;
import com.capestone.hrms_backend.repository.job.JobOpeningRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IJobOpeningService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobOpeningServiceImpl implements IJobOpeningService {
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileService;
    private final JobOpeningRepository jobOpeningRepository;
    private final JobCvReviewerRepository reviewerRepository;
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

    @Override
    public void addReviewer(Long jobOpeningId, List<Long> empIds) {
        JobOpening opening = jobOpeningRepository.findById(jobOpeningId).orElseThrow(()->new ResourceNotFoundException("Job Opening not found."));
        for(Long id : empIds){
            log.info("Employee id: {}",id);
            if(reviewerRepository.findByJobIdAndReveiwerId(jobOpeningId,id).isPresent()){
                continue;
            }

            Employee emp = employeeRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Employee not found."));
            log.info("Employee: {} {}",emp.getFirstName(),emp.getLastName());

            JobCvReviewer reveiwer = new JobCvReviewer();
            //Add Members
            reveiwer.setJob(opening);
            reveiwer.setReveiwer(emp);
            log.info("employee: {}",reveiwer.getReveiwer().getId());
            reviewerRepository.save(reveiwer);
        }
    }

    @Override
    public void removeReviewer(Long jobOpeningId, Long empId) {

    }

    @Override
    public List<JobOpeningReviewerResponseDto> getReviewers(Long jobOpeningId) {
        return List.of();
    }
}
