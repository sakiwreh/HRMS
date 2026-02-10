package com.capestone.hrms_backend.security;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HrmsUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUserEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found."));
        return new HrmsUserDetails(employee);
    }
}
