package com.capestone.hrms_backend.repository.organization;

import com.capestone.hrms_backend.dto.response.EmployeeLookupDto;
import com.capestone.hrms_backend.entity.organization.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
        e.department.name,
        e.profilePath
    )
    FROM Employee e
    ORDER BY e.firstName
    """)
    List<EmployeeLookupDto> findEmployeeLookup();

    List<Employee> findByRoleNameIgnoreCase(String rolename);


    @Query("""
           SELECT e FROM Employee e
           WHERE MONTH(e.dob) = MONTH(:today)
             AND DAY(e.dob) = DAY(:today)
           """)
    List<Employee> findEmployeesHavingBirthday(@Param("today") LocalDate today);

    @Query("""
           SELECT e FROM Employee e
           WHERE MONTH(e.doj) = MONTH(:today)
             AND DAY(e.doj) = DAY(:today)
           """)
    List<Employee> findEmployeesHavingAnniversary(@Param("today") LocalDate today);

}
