package com.example.demo.controllers;

import com.example.demo.dto.*;
import com.example.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "Операции с пользователями")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Создать пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Пользователь создан",
                            content = @Content(schema = @Schema(implementation = UserPostResponse.class))
                    ),
                    @ApiResponse(responseCode = "409", description = "Пользователь уже существует")
            }
    )
    @PostMapping
    public ResponseEntity<EntityModel<UserPostResponse>> createUser(
            @RequestBody UserPostRequest request) {
        UserPostResponse response = userService.createUser(request);
        EntityModel<UserPostResponse> model = EntityModel.of(response,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserByEmail(response.getEmail())).withRel("get-user"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .deleteUser(response.getId())).withRel("delete-user")
        );
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Обновить email пользователя",
            parameters = {
                    @Parameter(name = "id", description = "ID пользователя", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email обновлён",
                            content = @Content(schema = @Schema(implementation = UserUpdateResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @PutMapping("/{id}/email")
    public ResponseEntity<EntityModel<UserUpdateResponse>> updateUserEmail(
            @PathVariable Long id,
            @RequestBody UserUpdateRequest request) {
        UserUpdateResponse response = userService.updateUserEmail(id, request);
        EntityModel<UserUpdateResponse> model = EntityModel.of(response,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserByEmail(response.getEmail())).withRel("get-user")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Получить пользователя по email",
            parameters = {
                    @Parameter(name = "email", description = "Email пользователя", required = true)
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Пользователь найден",
                            content = @Content(schema = @Schema(implementation = UserGetResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @GetMapping
    public ResponseEntity<EntityModel<UserGetResponse>> getUserByEmail(
            @RequestParam String email) {
        UserGetRequest request = new UserGetRequest();
        request.setEmail(email);
        UserGetResponse response = userService.getUserByEmail(request);
        EntityModel<UserGetResponse> model = EntityModel.of(response,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .updateUserEmail(response.getId(), null)).withRel("update-email"),
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .deleteUser(response.getId())).withRel("delete-user")
        );
        return ResponseEntity.ok(model);
    }

    @Operation(
            summary = "Удалить пользователя",
            parameters = {
                    @Parameter(name = "id", description = "ID пользователя", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        UserDeleteRequest request = new UserDeleteRequest();
        request.setId(id);
        userService.deleteUser(request);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        if (message != null && message.contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        if (message != null && message.contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(message);
    }
}
