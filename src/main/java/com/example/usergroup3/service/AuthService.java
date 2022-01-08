package com.example.usergroup3.service;

import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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


    public void updateUser(Long id, User user) {
        User user1 = getUser(id);
        if (user1.getRole().equals("customer")) {
            user1.setId(id);
            user1.setName(user.getName());
            user1.setJobCount(0);
            user1.setRole("customer");
            user1.setAddress(user.getAddress());
            user1.setEmail(user.getEmail());
            user1.setPhone(user.getPhone());
            user1.setPassword(user1.getPassword());
            user1.setType(null);
        } else if (user1.getRole().equals("mechanic")) {
            user1.setId(id);
            user1.setName(user.getName());
            user1.setJobCount(user1.getJobCount());
            user1.setRole("mechanic");
            user1.setAddress(user.getAddress());
            user1.setEmail(user.getEmail());
            user1.setPhone(user.getPhone());
            user1.setPassword(user1.getPassword());
            user1.setType(user1.getType());
        }
        this.userRepository.save(user1);
    }

    public String updatePassWord (Long id, String oldPassword, String newPassword) {
        User user = getUser(id);
        boolean isMatch = passwordEncoder.matches(oldPassword, user.getPassword());
        if (isMatch) {
            user.setPassword(encodePassword(newPassword));
            userRepository.save(user);
            return "Success";
        } else {
            return "Fail";
        }
    }

    public void deleteUser (Long id) {
        User user = getUser(id);
        this.userRepository.delete(user);
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

    public Map<String, Object> getAllUserByRole(String role, int page, int size, String[] sort) {
        Map<String, Object> res = new HashMap<>();
        try {
            List<User> userList = new ArrayList<>();
            List<Order> orders = new ArrayList<Order>();

            if (sort[0].contains(",")) {
                // will sort more than 2 fields
                // sortOrder="field, direction"
                for (String sortOrder : sort) {
                    String[] _sort = sortOrder.split(",");
                    orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
                }
            } else {
                // sort=[field, direction]
                orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
            }

            Pageable paging = PageRequest.of(page,size,Sort.by(orders));

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

    public Map<String, Object> searchUser(String role, int page, int size, String keyword) {
        Map<String, Object> res = new HashMap<>();
        try {
            List<User> userList = new ArrayList<>();
            Pageable paging = PageRequest.of(page,size);
            Page<User> userPage;
            userPage = userRepository.search(keyword, paging);

            if(role.equals("customer")) {
                for (int i = 0; i < userPage.getContent().size(); i++) {
                    if (userPage.getContent().get(i).getRole().equals("customer")) {
                        userList.add(userPage.getContent().get(i));
                    }
                }
            } else if (role.equals("mechanic")) {
                for (int i = 0; i < userPage.getContent().size(); i++) {
                    if (userPage.getContent().get(i).getRole().equals("mechanic")) {
                        userList.add(userPage.getContent().get(i));
                    }
                }
            } else {
                userList = userPage.getContent();
            }

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


    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("asc")) {
            return Sort.Direction.ASC;
        } else if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }

        return Sort.Direction.ASC;
    }
}
