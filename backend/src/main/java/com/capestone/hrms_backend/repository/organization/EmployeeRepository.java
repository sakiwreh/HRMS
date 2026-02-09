package com.capestone.hrms_backend.repository.organization;

import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
