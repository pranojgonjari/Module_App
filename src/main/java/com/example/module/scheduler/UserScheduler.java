package com.example.module.scheduler;

import com.example.module.service.EmailService;
import com.example.module.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 0/10 * ? * *")
    public void fetchUsersAndSendMail(){
        log.info("Executing scheduled task: fetchUsersAndSendMail");
        // Logic to fetch users and send mail can be added here
    }
}
