package com.nexxserve.medadmin.dto.response;

import com.nexxserve.medadmin.entity.Admins;
import com.nexxserve.medadmin.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RegisterResponseDto {
    private Long id;
    private String username;
    private Role role;
    private String email;
    private String token;
    private String message;

    public static RegisterResponseDto fromEntity(Admins admin, String token, String message) {
        return new RegisterResponseDto(
                admin.getId(),
                admin.getUsername(),
                admin.getRole(),
                admin.getEmail(),
                token,
                message);
    }
}
