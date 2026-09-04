package Mapper;

import dto_response.UserResponse;
import entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        String role = null;
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            role = user.getRoles().get(0);
        }

        return UserResponse.builder()
                .id(user.getId() == null ? null : user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(role)
                .build();
    }
}
