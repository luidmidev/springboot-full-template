package com.luidmidev.template.spring.dto;


import com.luidmidev.template.spring.validation.Password;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class Register {

    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @NotBlank(message = "El nombre es obligatorio")
    @NotNull(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜ ]*$", message = "El nombre solo puede contener letras")
    private String name;

    @Size(min = 3, max = 100, message = "El apellido debe tener entre 3 y 100 caracteres")
    @NotBlank(message = "El apellido es obligatorio")
    @NotNull(message = "El apellido es obligatorio")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜ ]*$", message = "El apellido solo puede contener letras")
    private String lastname;


    @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
    @NotBlank(message = "El username es obligatorio")
    @NotNull(message = "El username es obligatorio")
    private String username;

    @Password
    private String password;

    @Size(min = 3, max = 100, message = "El email debe tener entre 3 y 100 caracteres")
    @NotBlank(message = "El email es obligatorio")
    @NotNull(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;


}

