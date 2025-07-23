package com.nexxserve.inventoryservice.mapper;

import com.nexxserve.inventoryservice.dto.user.UserDto;
import com.nexxserve.inventoryservice.entity.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .middleName(user.getMiddleName())
                .roles(user.getRoles())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }

        return User.builder()
                .email(userDto.getEmail())
                .phoneNumber(userDto.getPhoneNumber())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .middleName(userDto.getMiddleName())
                // Note: id, password, timestamps, and UserDetails fields are not set from DTO
                // These should be handled by the service layer or JPA lifecycle methods
                .build();
    }

    public void updateUserFromDto(UserDto userDto, User user) {
        if (userDto == null || user == null) {
            return;
        }

        // Only update fields that should be updatable from DTO
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getPhoneNumber() != null) {
            user.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getFirstName() != null) {
            user.setFirstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            user.setLastName(userDto.getLastName());
        }
        if (userDto.getMiddleName() != null) {
            user.setMiddleName(userDto.getMiddleName());
        }
        // Note: roles and status updates should typically be handled separately
        // with proper authorization checks in the service layer
    }
}