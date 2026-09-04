package Scheduler;

import cache.AppCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.UserRepositoryImpl;
import services.EmailService;

import java.util.Collections;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserSchedulerTest {

    @Mock
    private EmailService emailService;
    @Mock
    private UserRepositoryImpl userRepository;
    @Mock
    private AppCache appCache;

    @InjectMocks
    private UserScheduler userScheduler;

    @Test
    void testFetchUsersAndSendSaMail() {
        when(userRepository.getUserForSA()).thenReturn(Collections.emptyList());

        userScheduler.fetchUsersAndSendSaMail();

        verify(userRepository).getUserForSA();
    }
}
