package com.example.usergroup3.controller;

import com.example.usergroup3.dto.JobCountDto;
import com.example.usergroup3.dto.LoginRequest;
import com.example.usergroup3.model.User;
import com.example.usergroup3.service.AuthService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/auth/kafka")
public class AuthKafkaController {
    static final Logger logger = LoggerFactory.getLogger(AuthKafkaController.class);
    private final String TOPIC = "USER";

    @Autowired
    AuthService authService;

    @Autowired
    Gson gson;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @PostMapping("/signup")
    public void signup(@RequestBody User user) {
        String userJson = gson.toJson(user);
        logger.info(String.format("#### -> Produce signed up user -> %s", userJson));
        kafkaTemplate.send(TOPIC + "_SIGNUP", userJson);
    }

    @PostMapping("/addmechanic")
    public void addMechanic(@RequestBody User user) {
        String userJson = gson.toJson(user);
        logger.info(String.format("#### -> Produce created mechanic user -> %s", userJson));
        kafkaTemplate.send(TOPIC + "_CREATE_MECHANIC", userJson);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable(value = "id") Long id, @RequestBody User user) {
        user.setId(id);
        String userJson = gson.toJson(user);
        logger.info(String.format("#### -> Produce updated user -> %s", userJson));
        kafkaTemplate.send(TOPIC + "_UPDATE", userJson);
    }

    @PostMapping("mechanic/{id}")
    public void updateJobCount(@PathVariable(value = "id") Long id, @RequestParam String request) {
        JobCountDto jobCountDto = new JobCountDto(id, request);
        String jobCountDtoJson = gson.toJson(jobCountDto);
        logger.info(String.format("#### -> Produce job count update -> %s", jobCountDtoJson));
        kafkaTemplate.send(TOPIC + "_COUNT", jobCountDtoJson);
    }
}
