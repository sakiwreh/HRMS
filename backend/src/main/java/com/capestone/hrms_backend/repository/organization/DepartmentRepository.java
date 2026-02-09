package com.capestone.hrms_backend.repository.organization;

import com.capestone.hrms_backend.entity.organization.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
