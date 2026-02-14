package com.capestone.hrms_backend.repository.organization;

import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserEmail(String email);
    List<Employee> findByManagerId(Long managerId);
}
