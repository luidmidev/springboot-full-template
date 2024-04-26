package com.luidmidev.template.spring.services;


import com.luidmidev.template.spring.dto.Login;
import com.luidmidev.template.spring.dto.RecoveryPasswordData;
import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.models.Authority;
import com.luidmidev.template.spring.models.TokenForgetPassword;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.repositories.AuthorityRepository;
import com.luidmidev.template.spring.repositories.TokenForgetPasswordRepository;
import com.luidmidev.template.spring.repositories.UserRepository;
import com.luidmidev.template.spring.security.jwt.Jwt;
import com.waipersoft.email.EmailSenderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Transactional
@Log4j2
public class AuthenticationService {

    private static final Random random = new Random();

    private final UserRepository userRepository;
    private final TokenForgetPasswordRepository forgetPasswordRepository;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;
    private final SessionAuditService sessionAuditService;
    private final PasswordEncoder encoder;
    private final Jwt jwt;


    @Autowired
    public AuthenticationService(UserRepository userRepository, AuthorityRepository authorityRepository, TokenForgetPasswordRepository forgetPasswordRepository, EmailSenderService emailSenderService, Jwt jwt, AuthenticationManager authenticationManager, SessionAuditService sessionAuditService, PasswordEncoder encoder) {

        this.userRepository = userRepository;
        this.forgetPasswordRepository = forgetPasswordRepository;
        this.emailSenderService = emailSenderService;
        this.jwt = jwt;
        this.authenticationManager = authenticationManager;
        this.sessionAuditService = sessionAuditService;
        this.encoder = encoder;

        this.forgetPasswordRepository.deleteExpiredToken();

        var adminRole = authorityRepository.findByName("ADMIN").orElseGet(() -> {
            var role = Authority.builder()
                    .name("ADMIN")
                    .description("Rol de administrador")
                    .build();
            return authorityRepository.save(role);
        });

        if (!authorityRepository.existsByName("USER")) {
            var role = Authority.builder()
                    .name("USER")
                    .description("Rol de usuario")
                    .build();
            authorityRepository.save(role);
        }

        if (userRepository.existsByUsername("admin159")) {
            log.info("Ya existe un usuario administrador en el sistema, omitiendo creación");
            return;
        }

        var upperCase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var lowerCase = "abcdefghijklmnopqrstuvwxyz";
        var digits = "0123456789";
        var special = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        var alphabet = upperCase + lowerCase;
        var password = new StringBuilder();

        var list = List.of(upperCase, lowerCase, digits, special, alphabet);

        for (int i = 0; i < 8; i++) {
            var index = random.nextInt(list.size());
            var character = list.get(index).charAt(random.nextInt(list.get(index).length()));
            password.append(character);
        }

        password.append(UUID.randomUUID());

        var admin = User.builder()
                .username("admin159")
                .password(encoder.encode(password.toString()))
                .name("Administrador")
                .lastname("Administrador")
                .email("playerluis159@gmail.com")
                .enabled(true)
                .authorities(List.of(adminRole))
                .cedula("2300826357")
                .build();

        userRepository.save(admin);

        log.info("Usuario administrador creado con contraseña generada: {}", password.toString());

    }

    public String authenticate(Login login) {
        var authenticationToken = UsernamePasswordAuthenticationToken.unauthenticated(login.getUsername().trim(), login.getPassword());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var user = (User) authentication.getPrincipal();
        var token = this.jwt.create(user.getId().toString(), user.getUsername());
        sessionAuditService.saveActionUser(user, "Inicio de sesión");
        return token;
    }


    public void forgotPassword(String email) {
        var userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new ClientException("No existe un usuario con el correo electrónico ingresado");
        }

        var token = generateCode();
        saveTokenForgetPassword(email, token);
        emailSenderService.sendSimpleMail(email, "RECUPERACIÓN DE CUENTA", "Su código de recuperación de contraseña es: " + token);
        sessionAuditService.saveActionUser(userOptional.get(), "Solicitud de cambio de contraseña");
    }


    public void resetPassword(RecoveryPasswordData data) {

        var tokenOptional = forgetPasswordRepository.findByToken(data.getToken());

        if (tokenOptional.isEmpty()) {
            throw new ClientException("El código de recuperación de contraseña no es válido");
        }

        var token = tokenOptional.get();
        boolean expired = token.isExpired();

        if (expired) {
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
        return String.format("%08d", random.nextInt(100000000));
    }

}
