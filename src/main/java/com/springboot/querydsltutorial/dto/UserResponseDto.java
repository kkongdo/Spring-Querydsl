package com.springboot.querydsltutorial.dto;

import com.springboot.querydsltutorial.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;

    public static UserResponseDto of(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
