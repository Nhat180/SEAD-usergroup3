package com.example.usergroup3.controller;

import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public void signup (@RequestBody User user) {
        authService.signup(user);
    }

    @PostMapping("/login")
    public String login (@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/logout")
    public String logout() {
        return authService.logout();
    }

    @GetMapping("/currentuser")
    public String currentUserName(Principal principal)  {
        try {
            return principal.getName();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "You still not login";
        }
    }

}
