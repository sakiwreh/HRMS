package com.capestone.hrms_backend.dto.response;

import com.capestone.hrms_backend.entity.travel.DocType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TravelDocumentResponseDto {
    private Long id;
    private DocType docType;
    private String description;
    private String fileName;
    private Long fileSize;
    private Long uploadedById;
    private Long uploadedForId;
    private String uploadedByName;
    private String uploadedForName;
}