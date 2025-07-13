package com.example.demo.controller;

import com.example.demo.dto.errors.BadRequestErrorResponseDto;
import com.example.demo.dto.errors.ErrorResponseDto;
import com.example.demo.dto.users.CreateUserRequestDto;
import com.example.demo.dto.users.UpdateUserRequestDto;
import com.example.demo.dto.users.UserResponseDto;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@Tag(name = "user", description = "Manage a user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", // Updated summary
            description = "Create a new user", // Updated description
            operationId = "createUser") // Updated operationId
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User has been created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid details supplied",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "User with email already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUsers(@Valid @RequestBody CreateUserRequestDto user) {
        logger.info("Received request to register user: {}", user.email());
            UserResponseDto registeredUser = userService.registerNewUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }


    @Operation(summary = "Fetch user by ID.",
            description = "Fetch user by ID.",
            operationId = "fetchUserByID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user details",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the transaction",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("userId") Long userId) {
        logger.info("Received request to get user by ID: {}", userId);
        UserResponseDto user = userService.findUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user by ID.",
            description = "Update user by ID.",
            operationId = "updateUserByID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The updated user details",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the transaction",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{userId}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UpdateUserRequestDto updateUserRequestDto) {
        logger.info("Received request to update user with ID: {}", userId);
        UserResponseDto updatedUser = userService.updateUser(userId, updateUserRequestDto);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Delete user by ID.",
            description = "Delete user by ID.",
            operationId = "deleteUserByID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The user has been deleted"),
            @ApiResponse(responseCode = "400", description = "The request didn't supply all the necessary data",
                    content = @Content(schema = @Schema(implementation = BadRequestErrorResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Access token is missing or invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "The user is not allowed to access the transaction",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "A user cannot be deleted when they are associated with a bank account",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "An unexpected error occurred",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") Long userId) {
        logger.info("Received request to delete user with ID: {}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}

