package com.capestone.hrms_backend.service.impl;

import com.capestone.hrms_backend.dto.request.AuthRequestDto;
import com.capestone.hrms_backend.dto.response.AuthResponseDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.exception.BusinessException;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import com.capestone.hrms_backend.security.HrmsUserDetails;
import com.capestone.hrms_backend.service.IAuthService;
import com.capestone.hrms_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.module.ResolutionException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Override
    public AuthResponseDto login(AuthRequestDto request) {
        Employee employee = employeeRepository.findByUserEmail(request.getEmail())
                .orElseThrow(()->new ResolutionException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(),employee.getUser().getPassword()))
            throw new BusinessException("Invalid password");

        HrmsUserDetails hrmsUserDetails = new HrmsUserDetails(employee.getId(),employee.getUser().getEmail(),employee.getUser().getPassword(),employee.getRole().getName());
        String token = jwtUtil.generateToken(hrmsUserDetails);

        return AuthResponseDto.builder()
                .id(employee.getId())
                .name(employee.getFirstName()+" "+employee.getLastName())
                .email(employee.getUser().getEmail())
                .role(employee.getRole().getName())
                .token(token)
                .build();
    }
}
