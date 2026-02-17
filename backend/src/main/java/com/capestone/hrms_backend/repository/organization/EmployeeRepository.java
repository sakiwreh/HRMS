package com.capestone.hrms_backend.repository.organization;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByUserEmail(String email);
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findByManagerIsNull();
    @Query("""
    SELECT new com.capestone.hrms_backend.dto.response.EmployeeLookupDto(
        e.id,
        CONCAT(e.firstName,' ',e.lastName),
        e.user.email,
        e.designation,
        e.department.name
    )
    FROM Employee e
    ORDER BY e.firstName
    """)
    List<EmployeeLookupDto> findEmployeeLookup();

}
