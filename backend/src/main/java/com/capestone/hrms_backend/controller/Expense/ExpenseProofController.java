package com.capestone.hrms_backend.controller.Expense;

import com.capestone.hrms_backend.dto.response.ExpenseProofResponseDto;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IExpenseProofService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/expenses/proofs")
public class ExpenseProofController {
    private final IExpenseProofService expenseProofService;

    @PostMapping("/expenses/{id}/proofs")
    public ResponseEntity<ExpenseProofResponseDto> upload(@PathVariable Long id, @AuthenticationPrincipal HrmsUserDetails user, @RequestParam MultipartFile file,@RequestParam(required = false) String description) throws IOException {
        return ResponseEntity.ok(expenseProofService.upload(id,user.getEmployeeId(),description,file));
    }

    @GetMapping("/expenses/{id}/proofs")
    public ResponseEntity<List<ExpenseProofResponseDto>> getByExpenseId(@PathVariable Long id) throws IOException {
        return ResponseEntity.ok(expenseProofService.getByExpenseId(id));
    }

    @GetMapping("/expenses/proofs/{id}/download")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long id) throws IOException{
        byte[] data = expenseProofService.download(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).body(new ByteArrayResource(data));
    }

    @DeleteMapping("/expenses/proofs/{id}/delete")
    public ResponseEntity<String> delete(@PathVariable Long id,@AuthenticationPrincipal HrmsUserDetails user) throws IOException {
        expenseProofService.delete(id, user.getEmpId(), user.getRoleName());
        return ResponseEntity.ok("Document deleted successfully");
    }
}
