package controller;

import Mapper.UserMapper;
import dto_response.ApiResponse;
import dto_response.UserResponse;
import entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin APIs")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/all-users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<User> all = userService.getAll();
        if (all == null || all.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<List<UserResponse>>builder()
                            .success(false)
                            .message("No users found")
                            .build());
        }

        List<UserResponse> responses = all.stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.<List<UserResponse>>builder()
                .success(true)
                .message("Users fetched")
                .data(responses)
                .build());
    }
}
