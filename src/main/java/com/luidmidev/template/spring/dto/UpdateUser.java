package com.luidmidev.template.spring.dto;

import com.luidmidev.template.spring.validation.Password;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUser {

    private Boolean enabled;


    @Pattern(regexp = "^(ADMIN|USER)$", message = "El rol solo puede ser ADMIN o USER")
    private String role;


    @Password(nullable = true)
    private String password;
}
