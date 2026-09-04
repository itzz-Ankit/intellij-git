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

    @Mock
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;

    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
                .username("ayush")
                .email("ayush@example.com")
                .password("secret1")
                .build();
    }

    @Test
    void signupSavesUserAndReturnsMappedResponse() {
        when(userRepository.existsByUsername("ayush")).thenReturn(false);
        when(userRepository.existsByEmail("ayush@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(new ObjectId());
            return user;
        });

        UserResponse response = authService.signup(signupRequest);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("ayush", captor.getValue().getUsername());
        assertEquals("encoded", captor.getValue().getPassword());
        assertEquals("USER", captor.getValue().getRoles().get(0));
        assertEquals("ayush", response.getUsername());
        assertEquals("ayush@example.com", response.getEmail());
    }

    @Test
    void signupThrowsWhenUsernameExists() {
        when(userRepository.existsByUsername("ayush")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.signup(signupRequest));
    }

    @Test
    void loginReturnsJwtAuthResponse() {
        LoginRequest loginRequest = new LoginRequest("ayush", "secret1");
        User user = User.builder()
                .id(new ObjectId())
                .username("ayush")
                .email("ayush@example.com")
                .password("encoded")
                .roles(Collections.singletonList("USER"))
                .build();

        when(userRepository.findByUsername("ayush")).thenReturn(user);
        when(jwtService.generateToken("ayush")).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertEquals("jwt-token", response.getToken());
        assertEquals("ayush", response.getUsername());
        assertEquals("USER", response.getRole());
    }
}
