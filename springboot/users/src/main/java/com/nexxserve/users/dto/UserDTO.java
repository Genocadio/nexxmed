package com.nexxserve.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    @NotBlank(message = "First name is required")
    private String firstName;
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    private String lastName;

    private String username;

    private String phone;

    private String profileUrl;

    private String clinicId;
}