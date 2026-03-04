package com.shopsphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthValidationResponse {
    private boolean isAuthenticated;
    private String userId;
    private String email;
    private Set<String> roles;
}

