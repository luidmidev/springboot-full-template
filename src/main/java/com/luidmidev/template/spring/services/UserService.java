package com.luidmidev.template.spring.services;


import com.luidmidev.template.spring.dto.Register;
import com.luidmidev.template.spring.dto.UpdateUser;
import com.luidmidev.template.spring.exceptions.ClientException;
import com.luidmidev.template.spring.models.User;
import com.luidmidev.template.spring.repositories.AuthorityRepository;
import com.luidmidev.template.spring.repositories.UserRepository;
import com.luidmidev.template.spring.security.jwt.Jwt;
import com.waipersoft.email.EmailSenderService;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de la interfaz UserDetailsService de Spring Security que carga los detalles de un usuario.
 * Esta implementación utiliza UserRepository para obtener los detalles del usuario desde una fuente de datos.
 */
@Service
@Transactional
@Validated
public class UserService implements UserDetailsService {

    private final UserRepository repository;
    private final AuthorityRepository authorityRepository;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder encoder;
    private final SessionAuditService sessionAuditService;
    private final Jwt jwtutil;

    UserService(UserRepository repository, AuthorityRepository authorityRepository, EmailSenderService emailSenderService, PasswordEncoder encoder, SessionAuditService sessionAuditService, Jwt jwtutil) {
        this.repository = repository;
        this.authorityRepository = authorityRepository;
        this.emailSenderService = emailSenderService;
        this.encoder = encoder;
        this.sessionAuditService = sessionAuditService;
        this.jwtutil = jwtutil;
    }

    public Iterable<User> findAll() {
        return repository.findAll();
    }

    public User find(String id) {
        return repository.findById(id).orElseThrow(() -> new ClientException("El usuario no existe"));
    }


    public void updateDetails(String id, UpdateUser detailsUser) {

        var authorities = detailsUser.getAuthorities();
        var enabled = detailsUser.getEnabled();
        var password = detailsUser.getPassword();

        if (authorities == null && enabled == null && password == null) {
            throw new ClientException("No se ha enviado ningún dato para actualizar");
        }

        var user = repository.findById(id).orElseThrow(() -> new ClientException("El usuario no existe"));

        if (authorityRepository.existsAllByNameIn(authorities)) {
            throw new ClientException("Alguna de las autoridades enviadas no existe");
        }

        if (enabled != null) user.setEnabled(enabled);
        if (authorities != null) user.setAuthorities(authorityRepository.findAllByNameIn(authorities));
        if (password != null && !password.isBlank()) user.setPassword(encoder.encode(password));

        repository.save(user);

        sessionAuditService.saveActionUser(user, "Actualización de usuario");
        emailSenderService.sendSimpleMail(user.getEmail(), "ACTUALIZACIÓN DE DATOS", "Se han actualizado sus datos de usuario desde el sistema de administración de Turismo Urcuquí, si usted no ha solicitado esta acción, por favor contacte con el administrador del sistema. Sus datos de acceso son: \n" +
                "Usuario: " + user.getUsername() + "\n" +
                "Contraseña: " + (password != null && !password.isBlank() ? password : "No se ha actualizado") + "\n" +
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

        var userRole = authorityRepository.findByName("ROLE_USER").orElseThrow(() -> new ClientException("No se ha encontrado el rol de usuario"));

        var user = User.builder()
                .username(register.getUsername())
                .password(encoder.encode(register.getPassword()))
                .name(register.getName())
                .lastname(register.getLastname())
                .email(register.getEmail().trim())
                .enabled(true)
                .authorities(List.of(userRole))
                .build();


        var usersaved = repository.save(user);

        var jwt = jwtutil.create(usersaved.getId().toString(), usersaved.getUsername());

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
     * Este metod  se utiliza para cargar los detalles del usuario basados en el nombre de usuario.
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