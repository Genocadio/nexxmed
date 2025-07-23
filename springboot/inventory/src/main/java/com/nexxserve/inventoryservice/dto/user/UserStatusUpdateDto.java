package com.nexxserve.inventoryservice.dto.user;

import com.nexxserve.inventoryservice.enums.Role;
import com.nexxserve.inventoryservice.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

// User Status Update DTO (for admin operations)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusUpdateDto {

    private UserStatus status;
    private Set<Role> roles;
}
