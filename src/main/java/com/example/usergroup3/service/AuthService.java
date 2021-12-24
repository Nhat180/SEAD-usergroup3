package com.example.usergroup3.service;

import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public void signup (User user) {
        user.setPassword(encodePassword(user.getPassword()));
        userRepository.save(user);
    }


    public String login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new
                UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return authenticate.getName();
    }

    public String logout() {
        SecurityContextHolder.getContext().setAuthentication(null);
        return "Log out successfully";
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal
                = (org.springframework.security.core.userdetails.User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return this.userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
    }


    private String encodePassword (String password) {
        return passwordEncoder.encode(password);
    }
}
