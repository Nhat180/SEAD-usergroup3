package com.example.usergroup3.engine;

import com.example.usergroup3.dto.JobCountDto;
import com.example.usergroup3.model.User;
import com.example.usergroup3.service.AuthService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class AuthConsumer {
    final Logger logger = LoggerFactory.getLogger(AuthConsumer.class);
    final String TOPIC = "6c8s3gy1-default";
    final String GROUP_ID = "USER_ID";

    @Autowired
    private AuthService authService;

    @Autowired
    private Gson gson;

    @KafkaListener(topics = TOPIC + "_SIGNUP", groupId = GROUP_ID)
    public void signup(String userJson) {
        User user = gson.fromJson(userJson, User.class);
        logger.info(String.format("#### -> Consume signed up user -> %s", userJson));
        authService.signup(user);
    }

    @KafkaListener(topics = TOPIC  + "_CREATE_MECHANIC", groupId = GROUP_ID)
    public void addMechanic(String userJson) {
        User user = gson.fromJson(userJson, User.class);
        logger.info(String.format("#### -> Consume created mechanic user -> %s", userJson));
        authService.createMechanic(user);
    }

    @KafkaListener(topics = TOPIC + "_UPDATE", groupId = GROUP_ID)
    public void updateUser(String userJson) {
        User user = gson.fromJson(userJson, User.class);
        logger.info(String.format("#### -> Consume updated user -> %s", userJson));
        authService.updateUser(user.getId(), user);
    }

    @KafkaListener(topics = TOPIC + "_COUNT", groupId = GROUP_ID)
    public void updateJobCount(String jobCountDtoJson) {
        JobCountDto jobCountDto = gson.fromJson(jobCountDtoJson, JobCountDto.class);
        logger.info(String.format("#### -> Produce job count update -> %s", jobCountDtoJson));
        authService.updateJobCounter(jobCountDto.getId(), jobCountDto.getRequest());
    }
}
