package com.capestone.hrms_backend.service;

import com.capestone.hrms_backend.dto.response.ExpenseProofResponseDto;
import com.capestone.hrms_backend.dto.response.ExpenseResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IExpenseProofService {
    ExpenseProofResponseDto upload(Long expenseId, Long empId, String desc, MultipartFile file) throws IOException;
    List<ExpenseProofResponseDto> getByExpenseId(Long expenseId) throws IOException;
    byte[] download(Long proofId) throws IOException;
    void delete(Long proofId,Long requesterId, String role)throws IOException;
}
