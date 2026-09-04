package services;

import entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import repository.UserRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServicelmplTests {

    @InjectMocks
    private UserDetailsServicelmpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        // MockitoExtension initializes mocks
    }

    @Test
    void loadUserByUsernameTest() {
        when(userRepository.findByUsername("ram"))
                .thenReturn(User.builder()
                        .username("ram")
                        .password("encoded")
                        .roles(Collections.singletonList("USER"))
                        .build());

        UserDetails user = userDetailsService.loadUserByUsername("ram");

        assertNotNull(user);
        assertEquals("ram", user.getUsername());
    }
}
