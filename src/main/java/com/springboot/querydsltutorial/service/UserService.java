package com.springboot.querydsltutorial.service;

import com.springboot.querydsltutorial.dto.UserResponseDto;
import com.springboot.querydsltutorial.entity.User;
import com.springboot.querydsltutorial.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsersByName(String name) {
        List<User> userList = userRepository.findUsersByName(name);
        List<UserResponseDto> userResponseDtoList = userList.stream().map(UserResponseDto::of).collect(Collectors.toList());
        return userResponseDtoList;
    }
    @Transactional(readOnly = true)
    public List<UserResponseDto> findUsersByNameAndEmail(String name, String email) {
        List<User> users = userRepository.findUsersByNameAndEmail(name, email);
        List<UserResponseDto> userResponseDtoList = users.stream().map(UserResponseDto::of).collect(Collectors.toList());
        return userResponseDtoList;
    }
}
