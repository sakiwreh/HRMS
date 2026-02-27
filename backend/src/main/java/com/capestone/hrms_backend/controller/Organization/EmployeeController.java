package com.capestone.hrms_backend.controller.Organization;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.dto.response.EmployeeProfileDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IEmployeeService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final IEmployeeService employeeService;
    private final FileStorageService fileStorageService;

    @GetMapping("/lookup")
    public ResponseEntity<List<EmployeeLookupDto>> getEmployeeLookup(){
        return ResponseEntity.ok(employeeService.getEmployeeLookup());
    }

    @GetMapping("/me")
    public ResponseEntity<EmployeeProfileDto> getMyProfile(
            @AuthenticationPrincipal HrmsUserDetails user) {
        return ResponseEntity.ok(employeeService.getProfile(user.getEmployeeId()));
    }

    @PutMapping("/me")
    public ResponseEntity<EmployeeProfileDto> updateMyProfile(
            @AuthenticationPrincipal HrmsUserDetails user,
            @org.springframework.web.bind.annotation.RequestBody EmployeeProfileDto dto) {
        return ResponseEntity.ok(employeeService.updateProfile(user.getEmployeeId(), dto));
    }

    @PostMapping("/me/photo")
    public ResponseEntity<String> uploadProfilePhoto(@AuthenticationPrincipal HrmsUserDetails user, @org.springframework.web.bind.annotation.RequestParam("file") org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        String path = employeeService.uploadProfilePhoto(user.getEmployeeId(), file);
        return ResponseEntity.ok(path);
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<org.springframework.core.io.ByteArrayResource> downloadProfilePhoto(@PathVariable Long id) throws java.io.IOException {
        EmployeeProfileDto dto = employeeService.getProfile(id);
        if (dto.getProfilePath() == null) return ResponseEntity.notFound().build();
        byte[] data = fileStorageService.read(dto.getProfilePath());
        return ResponseEntity.ok().contentType(org.springframework.http.MediaType.IMAGE_JPEG).body(new org.springframework.core.io.ByteArrayResource(data));
    }
}
