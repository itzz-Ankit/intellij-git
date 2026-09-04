package services;

import Mapper.UserMapper;
import dto_request.LoginRequest;
import dto_request.SignupRequest;
import dto_response.AuthResponse;
import dto_response.UserResponse;
import entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(new ArrayList<>(Arrays.asList("USER")))
                .journalEntries(new ArrayList<>())
                .sentimentAnalysis(false)
                .build();

        User saved = userRepository.save(user);
        return UserMapper.toResponse(saved);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return AuthResponse.builder()
                .token(jwtService.generateToken(user.getUsername()))
                .username(user.getUsername())
                .role(user.getRoles() == null || user.getRoles().isEmpty()
                        ? "USER"
                        : user.getRoles().get(0))
                .build();
    }
}
