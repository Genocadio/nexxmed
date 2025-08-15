package com.nexxserve.inventoryservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private String message;
}
