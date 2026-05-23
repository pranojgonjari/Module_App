package com.example.module.scheduler;

import com.example.module.service.EmailService;
import com.example.module.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserSchedulerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserScheduler userScheduler;

    @Test
    public void testFetchUsersAndSendMail() {
        userScheduler.fetchUsersAndSendMail();
        // Since there's no logic yet, we just verify it executes without error
        // If logic is added later, we can verify interactions here
    }
}
