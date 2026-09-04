package com.vehicare.modules.auth.dto;

import com.vehicare.modules.user.entity.Role;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserAuthData {

    private Long id;
    private String email;
    private String password;
    private Role role;
    private boolean enabled;
}