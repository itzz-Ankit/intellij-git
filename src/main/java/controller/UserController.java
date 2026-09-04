package controller;

import Mapper.UserMapper;
import dto_response.ApiResponse;
import dto_response.UserResponse;
import entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import services.UserService;
import services.WeatherService;

@RestController
@Tag(name = "User APIs", description = "Read and Delete current user")
public class UserController {

    private final UserService userService;
    private final WeatherService weatherService;

    public UserController(UserService userService, WeatherService weatherService) {
        this.userService = userService;
        this.weatherService = weatherService;
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<String>> greeting(Authentication authentication) {
        weatherService.getWeather("Mumbai");
        String message = "Hi " + authentication.getName() + ", welcome to MindVault";
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Greeting")
                .data(message)
                .build());
    }

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(Authentication authentication) {
        User user = userService.findByUserName(authentication.getName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .success(true)
                .message("Profile fetched")
                .data(UserMapper.toResponse(user))
                .build());
    }

    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<Void>> deleteCurrentUser(Authentication authentication) {
        userService.deleteByUserName(authentication.getName());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("User deleted")
                .build());
    }
}
