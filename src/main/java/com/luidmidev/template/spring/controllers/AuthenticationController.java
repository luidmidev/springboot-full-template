package com.luidmidev.template.spring.controllers;

import com.luidmidev.template.spring.dto.Login;
import com.luidmidev.template.spring.dto.RecoveryPasswordData;
import com.luidmidev.template.spring.dto.Register;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.services.AuthenticationService;
import com.luidmidev.template.spring.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para la autenticación y gestión de usuarios.
 */
@Log4j2
@Validated
@RestController
public class AuthenticationController {
    private final AuthenticationService service;

    private final UserService userService;

    AuthenticationController(AuthenticationService service, UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    /**
     * Autentica un usuario.
     *
     * @param login El objeto LoginDTO que contiene las credenciales del usuario.
     * @return Una ResponseEntity que contiene el token JWT si la autenticación es exitosa, o un mensaje de error si las credenciales son inválidas.
     */
    @PostMapping(value = "/authenticate")
    public ResponseEntity<String> authenticate(@RequestBody Login login) {
        var jwt = service.authenticate(login);
        return ResponseEntity.ok().body(jwt);
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param register El objeto User que representa al nuevo usuario.
     * @return Una ResponseEntity que contiene el token JWT si el registro es exitoso, o un mensaje de error si ocurre algún problema.
     */
    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody Register register) {
        var jwt = userService.register(register);
        return ResponseEntity.ok().body(jwt);
    }

    /**
     * Actualiza los datos de un usuario.
     *
     * @param register   El objeto User que representa al usuario.
     * @param principals El objeto AuthenticationPrincipal que representa al usuario autenticado.
     * @return Una ResponseEntity que contiene el mensaje "Actualizado" si la actualización es exitosa, o un mensaje de error si ocurre algún problema.
     */
    @PutMapping(value = "/update")
    public ResponseEntity<String> update(@Valid @RequestBody Register register, @AuthenticationPrincipal User principals) {
        userService.update(register, principals);
        return ResponseEntity.ok().body("Actualizado");

    }


    /**
     * Elimina un usuario.
     * @param email El email del usuario a eliminar.
     * @return Una ResponseEntity que contiene el mensaje "Eliminado" si la eliminación es exitosa, o un mensaje de error si ocurre algún problema.
     */
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        service.forgotPassword(email);
        return ResponseEntity.ok().body("Hemos enviado un correo electrónico con el codigo de recuperación de cuenta");
    }


    /**
     * Actualiza la contraseña de un usuario.
     * @param data El objeto RecoveryPasswordData que contiene el email, la nueva contraseña y el token de recuperación.
     * @return Una ResponseEntity que contiene el mensaje "Se ha actualizado su contraseña" si la actualización es exitosa, o un mensaje de error si ocurre algún problema.
     */
    @PostMapping(value = "/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody RecoveryPasswordData data) {
        service.resetPassword(data);
        return ResponseEntity.ok().body("Se ha actualizado su contraseña");
    }

    /**
     * Obtiene los detalles de un usuario.
     *
     * @param principals El objeto AuthenticationPrincipal que representa al usuario autenticado.
     * @return Una ResponseEntity que contiene el objeto User si el usuario está autenticado, o un mensaje de error si no es válido.
     */
    @GetMapping(value = "/whoami")
    public ResponseEntity<User> whoami(@AuthenticationPrincipal User principals) {
        return ResponseEntity.ok().body(principals);
    }
}