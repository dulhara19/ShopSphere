package com.shopsphere.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserInternalDto {
    private UUID id;
    private String email;
    private String username;
    private List<String> roles;
}
