package io.springboot.learn.api;

import io.springboot.learn.model.AppRole;
import io.springboot.learn.model.AppUser;
import io.springboot.learn.model.AppUserRole;
import io.springboot.learn.service.AppUserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user-service/api/v1")
public class AppUserController {

    private final AppUserService appUserService;

    @GetMapping("/users")
    //@PreAuthorize("hasAuthority('ROLE_DEV','ROLE_PO','ROLE_DEVOPS','ROLE_QA')")
    public ResponseEntity<List<AppUser>> getAllUsers(){
        log.info("getAllUsers >> fetching all users");
        return new ResponseEntity<>(appUserService.getUsers(), HttpStatus.OK) ;
    }

    @GetMapping("/user/{userName}")
    //@PreAuthorize("hasAuthority('ROLE_DEV','ROLE_PO','ROLE_DEVOPS')")
    public ResponseEntity<AppUser> getUser(@PathVariable("userName") String userName){
        log.info("getUser >> fetching single user");
        Optional<AppUser> userOptional= Optional.ofNullable(appUserService.getUser(userName));
        AppUser appUser=userOptional.orElseThrow();
        return new ResponseEntity<>(appUser, HttpStatus.OK) ;
    }

    @PostMapping("/users")
    //@PreAuthorize("hasAuthority('ROLE_ADMIN','ROLE_PO','ROLE_DEV','ROLE_QA')")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser){
        log.info("saveUser >> saving user");
        return new ResponseEntity<>(appUserService.saveAppUser(appUser), HttpStatus.CREATED) ;
    }

    @PostMapping("/roles")
    //@PreAuthorize("hasAuthority('ROLE_TL','ROLE_PO','ROLE_ADMIN')")
    public ResponseEntity<AppRole> saveRole(@RequestBody AppRole appRole){
        log.info("saveRole >> saving role");
        return new ResponseEntity<>(appUserService.saveAppRole(appRole), HttpStatus.CREATED) ;
    }

    @PostMapping("/users/role")
    //@PreAuthorize("hasAuthority('ROLE_TL','ROLE_PO','ROLE_ADMIN')")
    public ResponseEntity<?> addUserRoles(@RequestBody AppUserRole appUserRole){
        log.info("addUserRoles >> saving role for user");
        appUserService.addAppRoleToUser(appUserRole.getUserName(),appUserRole.getAppRole());
        return new ResponseEntity<>(HttpStatus.OK) ;
    }


}
