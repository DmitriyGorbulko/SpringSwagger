package com.example.demo.mapper;

import com.example.demo.dto.*;
import com.example.demo.entities.User;

import java.util.Date;

public class UserMapper {


    public static User toEntity(UserPostRequest dto) {
        if (dto == null) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setCreatedAt(new Date());
        return user;
    }

    public static UserPostResponse toCreateResponse(User user) {
        if (user == null) return null;
        UserPostResponse response = new UserPostResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static void updateEntityEmail(UserUpdateRequest dto, User user) {
        if (dto == null || user == null) return;
        user.setEmail(dto.getEmail());
    }

    public static UserUpdateResponse toUpdateResponse(User user) {
        if (user == null) return null;
        UserUpdateResponse response = new UserUpdateResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static String toEmail(UserGetRequest dto) {
        if (dto == null) return null;
        return dto.getEmail();
    }

    public static UserGetResponse toGetResponse(User user) {
        if (user == null) return null;
        UserGetResponse response = new UserGetResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }

    public static Long toId(UserDeleteRequest dto) {
        if (dto == null) return null;
        return dto.getId();
    }
}
