package io.springboot.learn.service;

import io.springboot.learn.model.AppRole;
import io.springboot.learn.model.AppUser;
import io.springboot.learn.repository.AppRoleRepository;
import io.springboot.learn.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AppUserServiceImpl implements AppUserService, UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser user= appUserRepository.findByUserName(userName);

        if(user==null){
            log.error(userName+", user not found!");
            throw new RuntimeException("user not found!");
        }else{
            log.info(userName+", user found!");
        }

        Collection<SimpleGrantedAuthority> authorityCollection=new ArrayList<>();

        user.getRoles()
                .forEach(role->authorityCollection.add(
                        new SimpleGrantedAuthority(role.getRoleName())));

        return User
                .withUsername(user.getUserName())
                .password(user.getPassword())
                .authorities(authorityCollection).build();

    }

    @Override
    public AppUser saveAppUser(AppUser appUser) {
        log.info("saveAppUser>> saving user data: {}",appUser);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
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
        AppUser appUser=appUserRepository.findByUserName(userName);
        appUser.setPassword("***Confidential***");
        return appUser;
    }

    @Override
    public List<AppUser> getUsers() {
        List<AppUser> users=appUserRepository.findAll();
        log.info("getUsers>> get all users, count {}", (long) users.size());
        return maskUserPassword(users);
    }

    private List<AppUser> maskUserPassword(List<AppUser> appUsers){
        return appUsers.stream().filter(user->!user.getPassword().isEmpty())
                .map(user->{
                    AppUser user1=user;
                    user1.setPassword("***Confidential***");
                    return user1;
                }).collect(Collectors.toList());
    }


}
