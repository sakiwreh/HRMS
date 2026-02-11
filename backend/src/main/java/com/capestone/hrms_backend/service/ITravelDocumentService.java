package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.TravelDocumentResponseDto;
import com.capestone.hrms_backend.entity.travel.DocType;
import com.capestone.hrms_backend.entity.travel.TravelDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ITravelDocumentService {
    TravelDocumentResponseDto upload(Long travelId, Long uploaderId,Long uploadedFor, DocType type, String desc, MultipartFile file) throws IOException;
    List<TravelDocumentResponseDto> getByTravelId(Long travelId) throws IOException;
    byte[] download(Long docId) throws IOException;
    void delete(Long docId,Long requesterId, String role)throws IOException;
}
