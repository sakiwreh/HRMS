package com.capestone.hrms_backend.security;

import com.capestone.hrms_backend.entity.organization.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class HrmsUserDetails implements UserDetails {
    private final Employee employee;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_"+employee.getRole().getName()));
    }

    @Override
    public String getPassword() {
        return employee.getUser().getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getUser().getEmail();
    }

    public Long getEmployeeId(){
        return employee.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
