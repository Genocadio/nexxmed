package com.nexxserve.inventoryservice.dto.admin;

import com.nexxserve.inventoryservice.enums.Role;
import com.nexxserve.inventoryservice.enums.UserStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Data
public class UserReportDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserStatus Status;
    private Set<Role> roles;
    private String action;
    private LocalDateTime createdAt;
}
