package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.response.ExpenseProofResponseDto;
import com.capestone.hrms_backend.entity.expense.Expense;
import com.capestone.hrms_backend.entity.expense.ExpenseProof;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.exception.ResourceNotFoundException;
import com.capestone.hrms_backend.repository.expense.ExpenseProofRepository;
import com.capestone.hrms_backend.repository.expense.ExpenseRepository;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.service.IExpenseProofService;
import com.capestone.hrms_backend.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpenseProofServiceImpl implements IExpenseProofService {
    private final ExpenseProofRepository proofRepository;
    private final EmployeeRepository employeeRepository;
    private final ExpenseRepository expenseRepository;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;

    @Override
    public ExpenseProofResponseDto upload(Long expenseId, Long empId, String desc, MultipartFile file) throws IOException {

        //Fetch Expense
        Expense exp = expenseRepository.findById(expenseId).orElseThrow(()->new ResourceNotFoundException("Expense Not found."));
        //Fetch Employee
        Employee emp = employeeRepository.findById(empId).orElseThrow(()->new ResourceNotFoundException("Employee deosn't exist"));

        //Check if employee uploading for own expense
        if(exp.getEmployee().getId() != empId)throw new BusinessException("You can add proofs for your expense only.");

        //Saving file
        String path = fileStorageService.saveExp(expenseId, file);
        log.info("Path: {} {} {}", path,exp.getId(), emp.getId());

        //Creating entry Expense Proof
        ExpenseProof proof = new ExpenseProof();
        proof.setExpense(exp);
        proof.setDescription(desc);
        proof.setFileName(file.getOriginalFilename());
        proof.setFileSize(file.getSize());
        proof.setFileType(file.getContentType());
        proof.setFilePath(path);
        log.info("Saving | Entry Complete {}",path);
        proofRepository.save(proof);
        return modelMapper.map(proof, ExpenseProofResponseDto.class);
    }

    @Override
    public List<ExpenseProofResponseDto> getByExpenseId(Long expenseId) throws IOException {
        return proofRepository.findByExpenseId(expenseId)
                .stream()
                .map(m->modelMapper.map(m, ExpenseProofResponseDto.class))
                .toList();
    }

    @Override
    public byte[] download(Long proofId) throws IOException {
        ExpenseProof proof = proofRepository.findById(proofId).orElseThrow(()->new ResourceNotFoundException("Proof document doesn't exist."));
        return fileStorageService.read(proof.getFilePath());
    }

    @Override
    public void delete(Long proofId, Long requesterId, String role) throws IOException {
        ExpenseProof proof = proofRepository.findById(proofId).orElseThrow(()->new ResourceNotFoundException("Proof document doesn't exist."));
        //Delete from folder
        fileStorageService.delete(proof.getFilePath());
        //Delete from entry
        proofRepository.delete(proof);
    }
}
