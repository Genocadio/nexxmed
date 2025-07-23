package com.nexxserve.inventoryservice.dto.user;

import com.nexxserve.inventoryservice.enums.Role;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleUpdateDto {
    @NotEmpty
    private Set<Role> roles;
}