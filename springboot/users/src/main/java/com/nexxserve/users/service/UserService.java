package com.nexxserve.users.service;

import com.nexxserve.users.dto.UserDTO;
import com.nexxserve.users.exception.ResourceNotFoundException;
import com.nexxserve.users.exception.UserAlreadyExistsException;
import com.nexxserve.users.model.User;
import com.nexxserve.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return mapToDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO findUserBySearchParameter(String searchParameter) {
        if (searchParameter == null || searchParameter.trim().isEmpty()) {
            throw new IllegalArgumentException("Search parameter cannot be empty");
        }

        // Clean the search parameter
        String cleanParam = searchParameter.trim();

        // Check if the parameter is an email
        if (isEmail(cleanParam)) {
            return userRepository.findByEmail(cleanParam)
                    .map(this::mapToDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + cleanParam));
        }
        // Check if the parameter is a phone number
        else if (isPhoneNumber(cleanParam)) {
            return userRepository.findByPhone(cleanParam)
                    .map(this::mapToDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + cleanParam));
        }
        // Assume it's a username
        else {
            return userRepository.findByUsername(cleanParam)
                    .map(this::mapToDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + cleanParam));
        }
    }

    private boolean isEmail(String parameter) {
        // Simple regex for email validation
        return parameter.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private boolean isPhoneNumber(String parameter) {
        // Simple check for phone number - contains only digits, "+", "-", " ", "(" and ")"
        return parameter.matches("^[0-9+\\-\\s()]+$");
    }

    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        // Check if user already exists with this email
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new UserAlreadyExistsException("User already exists with email: " + userDTO.getEmail());
        }

        // Generate a unique user ID if not provided
        if (userDTO.getUserId() == null || userDTO.getUserId().isEmpty()) {
            userDTO.setUserId(UUID.randomUUID().toString());
        }

        User user = mapToEntity(userDTO);
        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Transactional
    public UserDTO updateUser(String userId, UserDTO userDTO) {
        User existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update user fields
        existingUser.setFirstName(userDTO.getFirstName());
        existingUser.setLastName(userDTO.getLastName());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setPhone(userDTO.getPhone());
        existingUser.setProfileUrl(userDTO.getProfileUrl());

        User updatedUser = userRepository.save(existingUser);
        return mapToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);
    }

    // Utility methods for mapping between Entity and DTO
    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhone(user.getPhone());
        userDTO.setProfileUrl(user.getProfileUrl());
        userDTO.setClinicId(user.getClinicId());
        return userDTO;
    }

    private User mapToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setPhone(userDTO.getPhone());
        user.setProfileUrl(userDTO.getProfileUrl());
        user.setClinicId(userDTO.getClinicId());
        return user;
    }
}