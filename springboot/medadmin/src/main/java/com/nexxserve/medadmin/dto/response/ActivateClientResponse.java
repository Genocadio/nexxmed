package com.nexxserve.medadmin.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ActivateClientResponse {
    private String token;
    private String refreshToken;
    private String message;
}