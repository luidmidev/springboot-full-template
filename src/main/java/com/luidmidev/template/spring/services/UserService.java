package com.luidmidev.template.spring.services;


import com.luidmidev.template.spring.dto.Register;
import com.luidmidev.template.spring.dto.UpdateUser;
import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.repositories.UserRepository;
import com.luidmidev.template.spring.security.Argon2CustomPasswordEncoder;
import com.luidmidev.template.spring.security.jwt.JWT;
import com.luidmidev.template.spring.services.emails.EmailSenderService;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.Optional;

/**
 * Implementación de la interfaz UserDetailsService de Spring Security que carga los detalles de un usuario.
 * Esta implementación utiliza UserRepository para obtener los detalles del usuario desde una fuente de datos.
 */
@Service
@Transactional
@Validated
public class UserService implements UserDetailsService {

    static final String[] ROLES = {"ADMIN", "USER"};

    private final UserRepository repository;
    private final EmailSenderService emailSenderService;
    private final Argon2CustomPasswordEncoder encoder;
    private final SessionAuditService sessionAuditService;
    private final JWT JWTUtil;

    UserService(UserRepository repository, EmailSenderService emailSenderService, Argon2CustomPasswordEncoder encoder, SessionAuditService sessionAuditService, JWT jwtUtil) {
        this.repository = repository;
        this.emailSenderService = emailSenderService;
        this.encoder = encoder;
        this.sessionAuditService = sessionAuditService;
        JWTUtil = jwtUtil;
    }

    public Iterable<User> findAll() {
        return repository
                .findAll()
                .stream()
                .map(this::hideEncodePassword)
                .toList();
    }

    public User find(String id) {
        Optional<User> user = repository.findById(id);
        if (user.isPresent()) {
            return hideEncodePassword(user.get());
        }
        throw new ClientException("El usuario no existe");
    }

    private User hideEncodePassword(User user) {
        user.setPassword(null);
        return user;
    }

    public void updateDetails(String id, UpdateUser detailsUser) {

        var role = detailsUser.getRole();
        var enabled = detailsUser.getEnabled();
        var password = detailsUser.getPassword();

        if (role == null && enabled == null && password == null) {
            throw new ClientException("No se ha enviado ningún dato para actualizar");
        }

        System.out.println(detailsUser);
        if (role != null && !Arrays.asList(ROLES).contains(role)) {
            throw new ClientException("El rol que se trata de asignar no existe en el sistema");
        }


        var user = repository.findById(id).orElseThrow(() -> new ClientException("El usuario no existe"));

        if (enabled != null) user.setEnabled(enabled);
        if (role != null) user.setRole(role);
        if (password != null && !password.isBlank()) user.setPassword(encoder.encode(password));


        repository.save(user);

        sessionAuditService.saveActionUser(user, "Actualización de usuario");
        emailSenderService.sendSimpleMail(user.getEmail(), "ACTUALIZACIÓN DE DATOS", "Se han actualizado sus datos de usuario desde el sistema de administración de Turismo Urcuquí, si usted no ha solicitado esta acción, por favor contacte con el administrador del sistema. Sus datos de acceso son: \n" +
                "Usuario: " + user.getUsername() + "\n" +
                "Contraseña: " + (password != null && !password.isBlank() ? password : "No se ha actualizado") + "\n" +
                "Rol: " + user.getRole() + "\n" +
                "Estado: " + (user.isEnabled() ? "Habilitado" : "Deshabilitado") + "\n" +
                "Cuenta no expirada: " + (user.isAccountNonExpired() ? "Si" : "No") + "\n" +
                "Cuenta no bloqueada: " + (user.isAccountNonLocked() ? "Si" : "No") + "\n"
        );
    }


    public String register(Register register) {

        if (repository.existsByUsername(register.getUsername())) {
            throw new ClientException("Nombre de usuario no disponible");
        }

        if (repository.existsByEmail(register.getEmail())) {
            throw new ClientException("El email ingresado ya está registrado");
        }

        var user = User.builder()
                .username(register.getUsername())
                .password(encoder.encode(register.getPassword()))
                .name(register.getName())
                .lastname(register.getLastname())
                .email(register.getEmail().trim())
                .enabled(true)
                .role("USER")
                .build();


        var usersaved = repository.save(user);

        var jwt = JWTUtil.create(usersaved.getId().toString(), usersaved.getUsername());

        sessionAuditService.saveActionUser(usersaved, "Registro de usuario");
        emailSenderService.sendSimpleMail(register.getEmail(), "BIENVENIDO A TURISMO URCUQUÍ", "Gracias por registrarse en nuestra aplicacion de realidad aumentada, esperamos que disfrute de su experiencia");

        return jwt;
    }

    public void update(Register register, User user) {

        var username = user.getUsername();
        var email = user.getEmail();

        if (!username.equals(register.getUsername()) && repository.existsByUsername(username)) {
            throw new ClientException("Nombre de usuario no disponible");
        }

        if (!email.equals(register.getEmail()) && repository.existsByEmail(email)) {
            throw new ClientException("El email ingresado ya está registrado");
        }

        user.setUsername(register.getUsername());
        user.setName(register.getName());
        user.setLastname(register.getLastname());
        user.setEmail(register.getEmail());

        if (register.getPassword() != null && !register.getPassword().isBlank()) {
            user.setPassword(encoder.encode(register.getPassword()));
        }

        repository.save(user);

        sessionAuditService.saveActionUser(user, "Actualización de usuario");
    }


    /**
     * Carga los detalles de un usuario por su nombre de usuario.
     * <p>
     * El método loadUserByUsername() se utiliza para cargar los detalles del usuario basados en el nombre de usuario.
     * Luego, estos detalles se utilizan para autenticar al usuario en el contexto de seguridad
     * de Spring y permitir el acceso a los recursos protegidos.
     *
     * @param username Nombre de usuario del usuario.
     * @return Los detalles del usuario como UserDetails.
     * @throws UsernameNotFoundException          Si no se encuentra ningún usuario con el nombre de usuario especificado.
     * @throws DataAccessResourceFailureException Si ocurre un error al acceder a la fuente de datos.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessResourceFailureException {
        Optional<User> userOptional = repository.findByUsernameOrEmail(username, username);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        throw new UsernameNotFoundException("El usuario con el nombre" + username + " no existe");
    }
}