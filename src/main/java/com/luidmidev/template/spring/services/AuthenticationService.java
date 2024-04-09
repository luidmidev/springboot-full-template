package com.luidmidev.template.spring.services;


import com.luidmidev.template.spring.dto.Login;
import com.luidmidev.template.spring.dto.RecoveryPasswordData;
import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.models.Role;
import com.luidmidev.template.spring.models.TokenForgetPassword;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.repositories.RoleRepository;
import com.luidmidev.template.spring.repositories.TokenForgetPasswordRepository;
import com.luidmidev.template.spring.repositories.UserRepository;
import com.luidmidev.template.spring.security.Argon2CustomPasswordEncoder;
import com.luidmidev.template.spring.security.jwt.Jwt;
import com.luidmidev.template.spring.services.emails.EmailSenderService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@Log4j2
public class AuthenticationService {

    private final UserRepository userRepository;
    private final TokenForgetPasswordRepository forgetPasswordRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;
    private final SessionAuditService sessionAuditService;
    private final Argon2CustomPasswordEncoder encoder;
    private final Jwt jwt;


    @Autowired
    public AuthenticationService(UserRepository userRepository, RoleRepository roleRepository, TokenForgetPasswordRepository forgetPasswordRepository, EmailSenderService emailSenderService, Jwt jwt, AuthenticationManager authenticationManager, SessionAuditService sessionAuditService, Argon2CustomPasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.forgetPasswordRepository = forgetPasswordRepository;
        this.emailSenderService = emailSenderService;
        this.jwt = jwt;
        this.authenticationManager = authenticationManager;
        this.sessionAuditService = sessionAuditService;
        this.encoder = encoder;

        this.forgetPasswordRepository.deleteExpiredToken();


        if (userRepository.existsByUsername("admin")) {
            log.info("Ya existe un usuario administrador en el sistema, omitiendo creación");
            return;
        }

        var adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            var role = Role.builder()
                    .name("ADMIN")
                    .description("Rol de administrador")
                    .build();
            return roleRepository.save(role);
        });

        roleRepository.findByName("USER").orElseGet(() -> {
            var role = Role.builder()
                    .name("USER")
                    .description("Rol de usuario")
                    .build();
            return roleRepository.save(role);
        });


        var admin = User.builder()
                .username("admin159")
                .password(encoder.encode("Qwerty1598."))
                .name("Administrador")
                .lastname("Administrador")
                .email("playerluis159@gmail.com")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .role(adminRole)
                .cedula("2300826357")
                .build();

        if (!userRepository.existsByUsername(admin.getUsername())) {
            log.info("Creando usuario administrador, sus credenciales son: {} - {}", admin.getUsername(), admin.getPassword());
            emailSenderService.sendSimpleMail(admin.getEmail(), "Se acaba de reiniciar el servidor", "Se acaba de reiniciar el servidor, sus credenciales del usuario administrador son: " + admin.getUsername() + " - " + admin.getPassword());
            userRepository.save(admin);
        }
    }

    public String authenticate(Login login) {
        var authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(login.getUsername().trim(), login.getPassword());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var user = (User) authentication.getPrincipal();
        var jwt = this.jwt.create(user.getId().toString(), user.getUsername());
        sessionAuditService.saveActionUser(user, "Inicio de sesión");
        return jwt;
    }


    public void forgotPassword(String email) {
        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            System.out.println(email);
            throw new ClientException("No existe un usuario con el correo electrónico ingresado");
        }

        var token = generateCode();
        saveTokenForgetPassword(email, token);
        emailSenderService.sendSimpleMail(email, "RECUPERACIÓN DE CUENTA - TURISMO URCIQUÍ", "Su código de recuperación de contraseña es: " + token);
        sessionAuditService.saveActionUser(userOptional.get(), "Solicitud de cambio de contraseña");
    }


    public void resetPassword(RecoveryPasswordData data) {

        var tokenOptional = forgetPasswordRepository.findByToken(data.getToken());

        if (tokenOptional.isEmpty()) {
            throw new ClientException("El código de recuperación de contraseña no es válido");
        }

        var token = tokenOptional.get();

        if (token.isExpired()) {
            forgetPasswordRepository.delete(token);
            throw new ClientException("El código de recuperación ha expirado, por favor solicite uno nuevo");
        }

        var userOptional = userRepository.findByEmail(token.getEmail());

        if (userOptional.isEmpty()) {
            throw new ClientException("No se pudo cambiar la contraseña, por favor intente nuevamente");
        }

        var user = userOptional.get();

        user.setPassword(encoder.encode(data.getPassword()));
        userRepository.save(user);
        forgetPasswordRepository.delete(token);
        sessionAuditService.saveActionUser(user, "Cambio de contraseña mediante código de recuperación");

    }

    private void saveTokenForgetPassword(String email, String token) {

        var tokenForgetPassword = TokenForgetPassword.builder()
                .email(email)
                .token(token)
                .build();

        forgetPasswordRepository.save(tokenForgetPassword);
    }


    private static String generateCode() {
        return String.format("%08d", new Random().nextInt(100000000));
    }

}
