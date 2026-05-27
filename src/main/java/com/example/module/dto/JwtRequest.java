// dto/JwtRequest.java
package com.example.module.dto;

import lombok.Data;

@Data
public class JwtRequest {
    private String email;
    private String password;
}