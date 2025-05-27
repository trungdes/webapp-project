package com.project.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.demo.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String string);

}
