package io.springboot.learn.repository;

import io.springboot.learn.model.AppRole;
import io.springboot.learn.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole,Long> {
    AppRole findByRoleName(String roleName);
}
