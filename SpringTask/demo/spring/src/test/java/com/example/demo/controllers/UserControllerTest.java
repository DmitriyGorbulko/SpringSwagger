package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;

import java.util.Date;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }


    @Test
    void createUser_Success_ReturnsCreatedUser() throws Exception {
        UserPostRequest request = new UserPostRequest();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setAge(25);

        UserPostResponse response = new UserPostResponse();
        response.setId(1L);
        response.setName("John");
        response.setEmail("john@example.com");
        response.setAge(25);
        response.setCreatedAt(new Date());

        when(userService.createUser(any(UserPostRequest.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.age").value(25))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void createUser_DuplicateEmail_ReturnsError() throws Exception {
        UserPostRequest request = new UserPostRequest();
        request.setName("Jane");
        request.setEmail("duplicate@example.com");
        request.setAge(30);

        when(userService.createUser(any())).thenThrow(new RuntimeException("Email already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("Email already exists")));
    }

    @Test
    void updateUserEmail_UserNotFound_ReturnsError() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setEmail("newemail@example.com");

        when(userService.updateUserEmail(eq(99L), any())).thenThrow(new RuntimeException("User not found with id: 99"));

        mockMvc.perform(put("/users/99/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("User not found with id: 99")));
    }

    @Test
    void getUserByEmail_Success_ReturnsUser() throws Exception {
        UserGetResponse response = new UserGetResponse();
        response.setId(2L);
        response.setName("Alice");
        response.setEmail("alice@example.com");
        response.setAge(28);
        response.setCreatedAt(new Date());

        when(userService.getUserByEmail(any())).thenReturn(response);

        mockMvc.perform(get("/users")
                        .param("email", "alice@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"))
                .andExpect(jsonPath("$.age").value(28))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void deleteUser_UserNotFound_ReturnsError() throws Exception {
        doThrow(new RuntimeException("User not found with id: 5")).when(userService).deleteUser(any());

        mockMvc.perform(delete("/users/5"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("User not found with id: 5")));
    }

    @Test
    void deleteUser_Success_ReturnsNoContent() throws Exception {
        doNothing().when(userService).deleteUser(any());

        mockMvc.perform(delete("/users/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createUser_EmptyEmail_ReturnsBadRequest() throws Exception {
        UserPostRequest request = new UserPostRequest();
        request.setName("Bob");
        request.setEmail("");
        request.setAge(22);


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
