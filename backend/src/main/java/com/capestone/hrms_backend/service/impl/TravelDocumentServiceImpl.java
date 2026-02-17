package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.TravelDocumentResponseDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.entity.travel.DocType;
import com.capestone.hrms_backend.entity.travel.TravelDocument;
import com.capestone.hrms_backend.entity.travel.TravelPlan;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.repository.travel.TravelDocumentRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanParticipantRepository;
import com.capestone.hrms_backend.repository.travel.TravelPlanRepository;
import com.capestone.hrms_backend.service.ITravelDocumentService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TravelDocumentServiceImpl implements ITravelDocumentService {

    private final TravelDocumentRepository travelDocumentRepository;
    private final TravelPlanRepository travelPlanRepository;
    private final TravelPlanParticipantRepository participantRepository;
    private final EmployeeRepository employeeRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    public TravelDocumentResponseDto upload(Long travelId, Long uploaderId, Long uploadedForId, DocType type, String desc, MultipartFile file) throws IOException {

        log.info("Init service layer");
        //Fetching Travel Plan, UploadedBy
        TravelPlan plan = travelPlanRepository.findById(travelId).orElseThrow(() -> new ResourceNotFoundException("Travel plan not found."));
        Employee uploadedBy = employeeRepository.findById(uploaderId).orElseThrow(() -> new ResourceNotFoundException("Employee doesn't exist."));

        // Only participants or HR can upload documents
        boolean isParticipant = participantRepository.findByTravelPlanIdAndEmployeeId(travelId, uploaderId).isPresent();
        boolean isCreator = plan.getCreatedBy() != null && plan.getCreatedBy().getId().equals(uploaderId);
        if (!isParticipant && !isCreator) {
            throw new BusinessException("You are not authorized to upload documents for this travel plan");
        }

        //Saving and getting path
        String path = fileStorageService.save(travelId, file);
        log.info("Plan & Travel read {} {}", plan.getId(), uploadedBy.getId());
        //Creating entry in Travel Document
        TravelDocument doc = new TravelDocument();
        doc.setTravelPlan(plan);
        doc.setUploadedBy(uploadedBy);
        if (uploadedForId != null) {
            doc.setUploadedFor(employeeRepository.findById(uploadedForId).orElseThrow(() -> new ResourceNotFoundException("Employee for which this is uploaded doesn't exist.")));
        }
        doc.setDocType(type);
        doc.setDescription(desc);
        doc.setFileName(file.getOriginalFilename());
        doc.setFileSize(file.getSize());
        doc.setFileType(file.getContentType());
        doc.setFilePath(path);
        log.info("Reaching Till Service: {}", file.getOriginalFilename());
        travelDocumentRepository.save(doc);
        return modelMapper.map(doc, TravelDocumentResponseDto.class);
    }

    @Override
    public List<TravelDocumentResponseDto> getByTravelId(Long travelId, Long empId, String role) throws IOException {
        boolean isHr = "HR".equalsIgnoreCase(role);
        boolean isParticipant = participantRepository.findByTravelPlanIdAndEmployeeId(travelId, empId).isPresent();
        if (!isHr && !isParticipant) {
            throw new BusinessException("You are not authorized to view documents for this travel plan");
        }
        return travelDocumentRepository.findByTravelPlanId(travelId)
                .stream()
                .map(doc -> modelMapper.map(doc, TravelDocumentResponseDto.class))
                .toList();
    }

    @Override
    public byte[] download(Long docId) throws IOException {
        TravelDocument document = travelDocumentRepository.findById(docId).orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return fileStorageService.read(document.getFilePath());
    }

    @Override
    @Transactional
    public void delete(Long docId, Long requesterId, String role) throws IOException {
        //Finding the doc to delete
        TravelDocument doc = travelDocumentRepository.findById(docId).orElseThrow(() -> new ResourceNotFoundException("Document doesn't exist"));

        boolean isUploader = doc.getUploadedBy() != null && doc.getUploadedBy().getId().equals(requesterId);
        boolean isHr = "HR".equalsIgnoreCase(role);
        if (!isUploader && !isHr) {
            throw new BusinessException("You are not authorized to delete this document");
        }

        //Deleting from file uploads
        fileStorageService.delete(doc.getFilePath());

        //Deleting from DB table
        travelDocumentRepository.delete(doc);
    }
}
