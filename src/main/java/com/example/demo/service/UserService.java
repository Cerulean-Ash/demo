package com.example.demo.service;

import com.example.demo.dto.users.CreateUserRequestDto;
import com.example.demo.dto.users.UpdateUserRequestDto;
import com.example.demo.dto.users.UserResponseDto;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UserAlreadyExistsException;
import com.example.demo.exception.UserHasAccountException;
import com.example.demo.model.User;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public UserService(UserRepository userRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder, SecurityService securityService) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Transactional
    public UserResponseDto registerNewUser(CreateUserRequestDto createUserRequestDto) {

        if (userRepository.findByEmail(createUserRequestDto.email()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + createUserRequestDto.email() + " already exists.");
        }

        User newUser = CreateUserRequestDto.toEntity(createUserRequestDto);

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User savedUser = userRepository.save(newUser);
        logger.info("User with email {} registered successfully with ID: {}", savedUser.getEmail(), savedUser.getId());
        return UserResponseDto.fromEntity(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDto findUserById(Long id) {

        User foundUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentAuthenticatedUser = securityService.getCurrentAuthenticatedUser();
        if (!foundUser.getId().equals(currentAuthenticatedUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access another user's details.");
        }

        return UserResponseDto.fromEntity(foundUser);
    }

    @Transactional
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto updateUserRequestDto) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentAuthenticatedUser = securityService.getCurrentAuthenticatedUser();
        if (!existingUser.getId().equals(currentAuthenticatedUser.getId())) {
            throw new AccessDeniedException("You do not have permission to update another user's details.");
        }

        if (updateUserRequestDto.email() != null && !updateUserRequestDto.email().trim().isEmpty()) {
            if (!existingUser.getEmail().equalsIgnoreCase(updateUserRequestDto.email())) {
                if (userRepository.findByEmail(updateUserRequestDto.email()).isPresent()) {
                    throw new UserAlreadyExistsException("Email " + updateUserRequestDto.email() + " is already taken.");
                }
            }
            existingUser.setEmail(updateUserRequestDto.email());
        }

        if (updateUserRequestDto.name() != null && !updateUserRequestDto.name().trim().isEmpty()) {
            existingUser.setName(updateUserRequestDto.name());
        }
        if (updateUserRequestDto.address() != null) {
            existingUser.setAddress(updateUserRequestDto.address());
        }
        if (updateUserRequestDto.phoneNumber() != null && !updateUserRequestDto.phoneNumber().trim().isEmpty()) {
            existingUser.setPhoneNumber(updateUserRequestDto.phoneNumber());
        }

        User updatedUser = userRepository.save(existingUser);
        logger.info("User with ID {} updated successfully.", updatedUser.getId());
        return UserResponseDto.fromEntity(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentAuthenticatedUser = securityService.getCurrentAuthenticatedUser();
        if (!userToDelete.getId().equals(currentAuthenticatedUser.getId())) {
            throw new AccessDeniedException("You do not have permission to delete another user.");
        }

        if (!accountRepository.findByUserId(id).isEmpty()) {
            throw new UserHasAccountException("User cannot be deleted as they have existing bank accounts.");
        }

        userRepository.deleteById(id);
        logger.info("User with ID {} deleted successfully.", id);
    }
}
