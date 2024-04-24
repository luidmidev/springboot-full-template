package com.luidmidev.template.spring.dto;


import com.luidmidev.template.spring.validation.Password;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Register {

    @Size(min = 3, max = 100)
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜ ]*$")
    private String name;

    @Size(min = 3, max = 100)
    @NotBlank
    @NotNull
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜ ]*$")
    private String lastname;


    @Size(min = 3, max = 100)
    @NotBlank
    @NotNull
    private String username;

    @Password
    private String password;

    @Size(min = 3, max = 100)
    @NotBlank
    @NotNull
    @Email
    private String email;


}

