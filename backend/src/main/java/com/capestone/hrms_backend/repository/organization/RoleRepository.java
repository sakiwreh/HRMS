package com.capestone.hrms_backend.repository.organization;

import com.capestone.hrms_backend.entity.organization.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}
