package com.luidmidev.template.spring.dto;


import com.luidmidev.template.spring.validation.Password;
import lombok.Data;

@Data
public class RecoveryPasswordData {
    @Password
    private String password;
    private String token;
}
