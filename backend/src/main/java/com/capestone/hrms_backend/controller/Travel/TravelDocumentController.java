package com.capestone.hrms_backend.controller.Travel;

import com.capestone.hrms_backend.dto.response.TravelDocumentResponseDto;
import com.capestone.hrms_backend.entity.travel.DocType;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.ITravelDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TravelDocumentController {

    private final ITravelDocumentService travelDocumentService;

    @PostMapping("/travel-plans/{id}/documents")
    public ResponseEntity<TravelDocumentResponseDto> upload(@PathVariable Long id, @RequestParam DocType docType, @RequestParam(required = false) String description, @RequestParam MultipartFile file, @RequestParam(required = false) Long uploadedFor ,@AuthenticationPrincipal HrmsUserDetails user) throws IOException{
        log.info("Controller layer: {} by {}",file.getOriginalFilename(),user.getEmployeeId());
        return ResponseEntity.ok(travelDocumentService.upload(id, user.getEmployeeId(),uploadedFor,docType,description,file));
    }

    @GetMapping("/travel-plans/{id}/documents")
    public ResponseEntity<List<TravelDocumentResponseDto>> getByTravelId(@PathVariable Long id,@AuthenticationPrincipal HrmsUserDetails user) throws IOException{
        return ResponseEntity.ok(travelDocumentService.getByTravelId(id, user.getEmpId(), user.getRoleName()));
    }

    @GetMapping("/travel-plan/documents/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) throws IOException{
        byte[] data = travelDocumentService.download(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new ByteArrayResource(data));
    }

    @DeleteMapping("/travel-plan/{id}/documents/delete")
    public ResponseEntity<String> delete(@PathVariable Long id,@AuthenticationPrincipal HrmsUserDetails user) throws IOException{
        travelDocumentService.delete(id, user.getEmployeeId(), user.getRoleName());
        return ResponseEntity.ok("Document deleted successfully");
    }
}
