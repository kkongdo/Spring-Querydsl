package com.springboot.querydsltutorial.repository;

import com.springboot.querydsltutorial.entity.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findUsersByName(String name);
    List<User> findUsersByNameAndEmail(String name, String email);
}