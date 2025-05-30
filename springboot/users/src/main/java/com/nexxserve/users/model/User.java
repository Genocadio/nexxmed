package com.nexxserve.users.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true)
    private String userId;  // This will be generated and match the Auth Service's user ID

    @NotBlank
    private String firstName;

    private String lastName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String profileUrl;

    @Column(name = "clinic_id")
    private String clinicId;

    private String username;

    // We don't store sensitive auth info like passwords or roles here
    // Those are stored in Auth Service
}