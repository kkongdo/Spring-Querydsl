package com.springboot.querydsltutorial.dto;

import com.springboot.querydsltutorial.entity.Post;
import com.springboot.querydsltutorial.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserResponseDto {
    private final Long id;
    private final String name;
    private final String email;
    private final List<Post> posts;

    public static UserResponseDto of(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPosts()
        );
    }
}
