package com.upeu.auth.dto;

import lombok.Data;

@Data
public class AuthRegisterRequest {
    private String username;
    private String password;
}