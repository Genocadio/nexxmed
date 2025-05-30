package com.nexxserve.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;
    private String profileUrl;
    private String clinicId;

    private String errorMessage;
    private Integer errorCode;

    public boolean hasError() {
        return errorMessage != null && !errorMessage.isEmpty();
    }
}
