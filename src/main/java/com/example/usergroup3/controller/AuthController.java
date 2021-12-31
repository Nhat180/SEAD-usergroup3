package com.example.usergroup3.controller;

import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/signup")
    public void signup(@RequestBody User user) {
        authService.signup(user);
    }

    @PostMapping("/signup/many")
    public void signup(@RequestBody User[] users) {
        for (User user : users) {
            authService.signup(user);
        }
    }

    @PostMapping("/addmechanic")
    public void addMechanic(@RequestBody User user) {
        authService.createMechanic(user);
    }

    @PostMapping("/addmechanic/many")
    public void addMechanic(@RequestBody User[] users) {
        for (User user : users) {
            authService.createMechanic(user);
        }
    }


    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public String logout() {
        return authService.logout();
    }

    @GetMapping("/currentuser")
    public String currentUserName(Principal principal) {
        try {
            return principal.getName();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "You still not login";
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
            @RequestParam(defaultValue = "5") int size
    ) {
        return new ResponseEntity<>(authService.getAllUserByRole(role, page, size), HttpStatus.OK);
    }

    @GetMapping("mechanic/getall/type")
    public List<User> getAllMechanicByType(@RequestParam(name = "request") String type) {
        return authService.getAvailableMechanicByType(type);
    }

    @PostMapping("mechanic/{id}")
    public void updateJobCount(@PathVariable(value = "id") Long id, @RequestParam String request) {
        authService.updateJobCounter(id, request);
    }

    @GetMapping
    public String hello() {
        return "Hello world";
    }

}
