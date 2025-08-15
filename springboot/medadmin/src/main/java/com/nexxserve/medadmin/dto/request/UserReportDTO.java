package com.nexxserve.medadmin.dto.request;

import com.nexxserve.medadmin.entity.clients.User;
import com.nexxserve.medadmin.enums.Role;
import com.nexxserve.medadmin.enums.UserStatus;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Getter
@Setter
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
    public User toEntity() {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setStatus(Status);
        user.setRole(roles);
        return user;
    }
}
