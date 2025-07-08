package com.example.demo.services;

import com.example.demo.config.KafkaProducerService;
import com.example.demo.dto.*;
import com.example.demo.entities.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.repositories.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final String CIRCUIT_BREAKER_NAME = "userServiceCB";
    private final KafkaProducerService kafkaProducerService;

    public UserService(UserRepository userRepository, KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "createUserFallback")
    public UserPostResponse createUser(UserPostRequest request) {
        User user = UserMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        kafkaProducerService.sendUserEvent(new UserEvent(savedUser.getEmail(), "CREATE"));
        return UserMapper.toCreateResponse(savedUser);
    }

    public UserPostResponse createUserFallback(UserPostRequest request, Throwable t) {
        return new UserPostResponse("Service temporarily unavailable. Please try again later.");
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "updateUserEmailFallback")
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

    public UserUpdateResponse updateUserEmailFallback(Long id, UserUpdateRequest request, Throwable t) {
        // Логирование ошибки и возврат заглушки или кастомного ответа
        return new UserUpdateResponse("Service temporarily unavailable. Please try again later.");
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "getUserByEmailFallback")
    public UserGetResponse getUserByEmail(UserGetRequest request) {
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found with email: " + request.getEmail());
        }
        return UserMapper.toGetResponse(optionalUser.get());
    }

    public UserGetResponse getUserByEmailFallback(UserGetRequest request, Throwable t) {
        // Возвращаем заглушку или выбрасываем исключение с понятным сообщением
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }

    @CircuitBreaker(name = CIRCUIT_BREAKER_NAME, fallbackMethod = "deleteUserFallback")
    public void deleteUser(UserDeleteRequest request) {
        Long id = UserMapper.toId(request);
        userRepository.findById(id).ifPresent(user -> {
            userRepository.deleteById(id);
            kafkaProducerService.sendUserEvent(new UserEvent(user.getEmail(), "DELETE"));
        });
    }

    public void deleteUserFallback(UserDeleteRequest request, Throwable t) {
        // Можно логировать ошибку, выбросить исключение или заглушку
        throw new RuntimeException("Service temporarily unavailable. Please try again later.");
    }
}
