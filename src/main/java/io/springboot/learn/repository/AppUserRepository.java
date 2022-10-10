package io.springboot.learn.repository;

import io.springboot.learn.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser,Long> {

    AppUser findByUserName(String username);
}
