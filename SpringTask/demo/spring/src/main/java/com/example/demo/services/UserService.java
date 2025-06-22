package com.example.demo.services;

import com.example.demo.dto.*;
import com.example.demo.entities.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserPostResponse createUser(UserPostRequest request) {
        User user = UserMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        return UserMapper.toCreateResponse(savedUser);
    }

    public UserUpdateResponse updateUserEmail(Long id, UserUpdateRequest request) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with id: " + id);
        }
        User user = optionalUser.get();
        UserMapper.updateEntityEmail(request, user);
        User updatedUser = userRepository.save(user);
        return UserMapper.toUpdateResponse(updatedUser);
    }

    public UserGetResponse getUserByEmail(UserGetRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + request.getEmail());
        }
        return UserMapper.toGetResponse(optionalUser.get());
    }

    public void deleteUser(UserDeleteRequest request) {
        Long id = UserMapper.toId(request);
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
