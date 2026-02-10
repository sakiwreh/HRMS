package com.capestone.hrms_backend.security;

import com.capestone.hrms_backend.entity.organization.Employee;
import com.capestone.hrms_backend.repository.organization.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HrmsUserDetailsService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByUserEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found."));
        String roleName = employee.getRole().getName();
        return new HrmsUserDetails(employee.getId(),employee.getUser().getEmail(),employee.getUser().getPassword(),roleName);
    }
}
