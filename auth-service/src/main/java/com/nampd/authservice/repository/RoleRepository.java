package com.nampd.authservice.repository;

import com.nampd.authservice.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByName(String name);

    Role findByName(String roleName);

    Role findByNameAndDepartment(String name, String department);

    List<Role> findByDepartment(String departmentName);
}