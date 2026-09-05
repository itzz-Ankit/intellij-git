package services;

import dto_request.LoginRequest;
import dto_request.SignupRequest;
import dto_response.AuthResponse;
import dto_response.UserResponse;
import entity.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import repository.UserRepository;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @InjectMocks private AuthService authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
                .username("itzzankit")
                .email("itzzankit@example.com")
                .password("secret1")
                .build();
    }

    @Test
    void signupSavesUserAndReturnsMappedResponse() {
        when(userRepository.existsByUsername("itzzankit")).thenReturn(false);
        when(userRepository.existsByEmail("itzzankit@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(new ObjectId());
            return user;
        });

        UserResponse response = authService.signup(signupRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("itzzankit", captor.getValue().getUsername());
        assertEquals("itzzankit@example.com", captor.getValue().getEmail());
        assertEquals("encoded", captor.getValue().getPassword());
        assertEquals(Collections.singletonList("USER"), captor.getValue().getRoles());
        assertEquals("USER", response.getRole());
    }

    @Test
    void signupThrowsWhenUsernameExists() {
        when(userRepository.existsByUsername("itzzankit")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.signup(signupRequest));
    }

    @Test
    void signupThrowsWhenEmailExists() {
        when(userRepository.existsByUsername("itzzankit")).thenReturn(false);
        when(userRepository.existsByEmail("itzzankit@example.com")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.signup(signupRequest));
    }

    @Test
    void loginReturnsJwtAuthResponse() {
        LoginRequest request = new LoginRequest("itzzankit", "secret1");
        User user = User.builder()
                .id(new ObjectId())
                .username("itzzankit")
                .email("itzzankit@example.com")
                .password("encoded")
                .roles(Collections.singletonList("USER"))
                .build();
        when(userRepository.findByUsername("itzzankit")).thenReturn(user);
        when(jwtService.generateToken("itzzankit")).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwt-token", response.getToken());
        assertEquals("itzzankit", response.getUsername());
        assertEquals("USER", response.getRole());
    }
}
