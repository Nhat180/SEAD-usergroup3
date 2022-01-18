package com.example.usergroup3.controller;

import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

//    @PostMapping("/signup")
//    public void signup(@RequestBody User user) {
//        authService.signup(user);
//    }

    // This one is for quick data insertion so there is no need for kafka
    @PostMapping("/signup/many")
    public void signup(@RequestBody User[] users) {
        for (User user : users) {
            authService.signup(user);
        }
    }

//    @PostMapping("/addmechanic")
//    public void addMechanic(@RequestBody User user) {
//        authService.createMechanic(user);
//    }

    // This one is for quick data insertion so there is no need for kafka
    @PostMapping("/addmechanic/many")
    public void addMechanic(@RequestBody User[] users) {
        for (User user : users) {
            authService.createMechanic(user);
        }
    }

//    @PutMapping("/{id}")
//    public void updateUser (@PathVariable(value = "id") Long id, @RequestBody User user) {
//        authService.updateUser(id, user);
//    }

    @PutMapping("/password/{id}")
    public String updatePassword (
            @PathVariable(value = "id") Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword
    ) {
        return authService.updatePassWord(id, oldPassword, newPassword);
    }

    @DeleteMapping("/{id}")
    public void deleteUser (@PathVariable(value = "id") Long id) {
        authService.deleteUser(id);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public String logout() {
        return authService.logout();
    }

//    @GetMapping("/currentuser")
//    public String currentUserName(Principal principal) {
//        try {
//            return principal.getName();
//        } catch (NullPointerException e) {
//            e.printStackTrace();
//            return "You still not login";
//        }
//    }

    // No redis
    @GetMapping("/currentuser")
    public User currentUserName(@AuthenticationPrincipal OAuth2User oAuth2User,
                                Principal principal)  {
        try {
            User user = new User();
            user.setId(Long.parseLong(principal.getName()));
            user.setName(oAuth2User.getAttribute("name"));
            user.setEmail(oAuth2User.getAttribute("email"));
            user.setRole("customer");
            user.setType(null);
            user.setPhone(null);
            user.setAddress(null);
            user.setJobCount(0);
            user.setPassword(null);
            return user;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable(value = "id") Long id) {
        return authService.getUser(id);
    }

    @GetMapping("/getall")
    public ResponseEntity<Map<String, Object>> getAllUserByRole(
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String keyword
    ) {
        return new ResponseEntity<>(authService.getAllUserByRole(role,page,size,sort,keyword), HttpStatus.OK);
    }


    @GetMapping("mechanic/getall/type")
    public List<User> getAllMechanicByType(@RequestParam(name = "request") String type) {
        return authService.getAvailableMechanicByType(type);
    }

//    @PostMapping("mechanic/{id}")
//    public void updateJobCount(@PathVariable(value = "id") Long id, @RequestParam String request) {
//        authService.updateJobCounter(id, request);
//    }

    @GetMapping
    public String hello() {
        return "Hello Nhat world";
    }

}
