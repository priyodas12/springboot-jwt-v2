package io.springboot.learn.service;

import io.springboot.learn.model.AppRole;
import io.springboot.learn.model.AppUser;

import java.util.List;

public interface AppUserService {

    AppUser saveAppUser(AppUser appUser);
    AppRole saveAppRole(AppRole appRole);
    void addAppRoleToUser(String username,String appRole);
    AppUser getUser(String userName);
    List<AppUser> getUsers();

}
