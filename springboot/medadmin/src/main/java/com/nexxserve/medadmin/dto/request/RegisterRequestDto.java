package com.nexxserve.medadmin.dto.request;

import com.nexxserve.medadmin.entity.Admins;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String password;

    public Admins toEntity() {
        Admins admins = new Admins();
        admins.setUsername(username);
        admins.setEmail(email);
        admins.setFirstName(firstName);
        admins.setLastName(lastName);
        admins.setPhoneNumber(phoneNumber);
        return admins;
    }
}
