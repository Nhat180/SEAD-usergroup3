package com.example.usergroup3.service;

import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        user.setRole("customer");
        userRepository.save(user);
    }


    public void createMechanic (User user) {
        user.setPassword(encodePassword(user.getPassword()));
        user.setRole("mechanic");
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

    public User getUser(Long id) {
        User user = new User();
        try {
            user = this.userRepository.findById(id)
                    .orElseThrow(() -> new Exception("User not found:: " + id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public Map<String, Object> getAllUserByRole(String role, int page, int size) {
        Map<String, Object> res = new HashMap<>();
        try {
            List<User> userList = new ArrayList<>();
            Pageable paging = PageRequest.of(page,size);

            Page<User> userPage;

            // paging based on the request of page and size from front-end
            if (role == null) {
                userPage = userRepository.findAll(paging);
            } else {
                userPage = userRepository.findAllByRole(role, paging);
            }

            userList = userPage.getContent(); // Assign paging content to list and then return to UI

            res.put("users", userList);
            res.put("currentPage", userPage.getNumber());
            res.put("totalUser", userPage.getTotalElements());
            res.put("totalPages", userPage.getTotalPages());

            return res;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getAvailableMechanicByType (String type) {
        List<User> mechanics = this.userRepository.findAllByType(type);
        for (int i = 0; i < mechanics.size(); i++) {
            if (mechanics.get(i).getJobCount() >= 2) {
                mechanics.remove(i);
            }
        }
        return mechanics;
    }

    public void updateJobCounter(Long id, String request) {
        User mechanic = getUser(id);
        if (request.equals("increase")) {
            mechanic.setJobCount(mechanic.getJobCount() + 1);
        } else {
            mechanic.setJobCount(mechanic.getJobCount() - 1);
        }
        this.userRepository.save(mechanic);
    }


    private String encodePassword (String password) {
        return passwordEncoder.encode(password);
    }
}
