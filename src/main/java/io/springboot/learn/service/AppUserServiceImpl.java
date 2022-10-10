package io.springboot.learn.service;

import io.springboot.learn.model.AppRole;
import io.springboot.learn.model.AppUser;
import io.springboot.learn.repository.AppRoleRepository;
import io.springboot.learn.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserServiceImpl implements AppUserService{

    private final AppUserRepository appUserRepository;

    @Autowired
    private final AppRoleRepository appRoleRepository;

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        log.info("saveAppUser>> saving user data: {}",appUser);
        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole saveAppRole(AppRole appRole) {
        log.info("saveAppRole>> saving user role data: {}",appRole);
        return appRoleRepository.save(appRole);
    }

    @Override
    public void addAppRoleToUser(String userName, String appRole) {
        AppUser appUser=appUserRepository.findByUserName(userName);
        AppRole appRole1=appRoleRepository.findByRoleName(appRole);

        appUser.getRoles().add(appRole1);
        log.info("addAppRoleToUser>> saving role {} to user : {}",appRole,userName);
        appUserRepository.save(appUser);
    }

    @Override
    public AppUser getUser(String userName) {
        log.info("getUser>> get user by username {}",userName);
        return appUserRepository.findByUserName(userName);
    }

    @Override
    public List<AppUser> getUsers() {
        List<AppUser> users=appUserRepository.findAll();
        log.info("getUsers>> get all users, count {}", (long) users.size());
        return appUserRepository.findAll();
    }
}
