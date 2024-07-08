package com.springboot.querydsltutorial.controller;

import com.springboot.querydsltutorial.dto.UserResponseDto;
import com.springboot.querydsltutorial.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponseDto> getUsersByName(@RequestParam("name") String name) {
        return userService.findUsersByName(name);
    }

    @GetMapping("/users/search")
    public List<UserResponseDto> getUsersByNameAndEmail(@RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String email) {
        return userService.findUsersByNameAndEmail(name, email);
    }
    // /users/search : 모든 사용자를 반환한다.
    // /users/search?name=안녕 : 이름이 안녕인 사용자의 데이터를 모두 반환한다.
    // /users/search?email=hello@example.com : 이메일이 hello@example.com인 사용자의 데이터를 모두 반환한다.
    // /users/search?name=안녕&email=hello@example.com : 이름이 "안녕"이고 이메일이 "hello@example.com"인 사용자를 반환한다.
}
