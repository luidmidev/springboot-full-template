package com.luidmidev.template.spring.dto;

import com.luidmidev.template.spring.validation.Password;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUser {

    private Boolean enabled;

    private List<String> authorities;

    @Password(nullable = true)
    private String password;
}
