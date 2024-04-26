package com.luidmidev.template.spring.security;


import com.luidmidev.template.spring.exceptions.ErrorResponse;
import com.luidmidev.template.spring.security.filters.JwtOncePerRequestFilter;
import com.luidmidev.template.spring.services.UserService;
import com.luidmidev.template.spring.utils.EnvironmentChecker;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.*;
import org.springframework.security.crypto.scrypt.SCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.HashMap;
import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * Clase de configuración de seguridad para Spring Security.
 */
@Log4j2
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final UserService securityUserDetailsService;
    private final JwtOncePerRequestFilter jwtAuthenticationFilter;
    private final EnvironmentChecker environmentChecker;

    @Autowired
    WebSecurityConfig(UserService securityUserDetailsService, JwtOncePerRequestFilter jwtAuthenticationFilter, EnvironmentChecker environmentChecker) {
        this.securityUserDetailsService = securityUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.environmentChecker = environmentChecker;
    }

    /**
     * Configura las cadenas de filtros de seguridad para las solicitudes HTTP.
     *
     * @param http Objeto HttpSecurity para la configuración de seguridad.
     * @return Cadena de filtros de seguridad.
     * @throws Exception Si se produce un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("Configurando seguridad de la aplicación");
        http.headers(head -> head.frameOptions(FrameOptionsConfig::disable));

        log.info("Configurando CSRF");
        http.csrf(CsrfConfigurer::disable);

        log.info("Configurando CORS");
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        log.info("Configurando autorización de solicitudes HTTP");
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        antMatcher(HttpMethod.GET, "/files/**")
                ).permitAll()
                .requestMatchers(
                        "/",
                        "/authenticate",
                        "/register",
                        "/forgot-password",
                        "/reset-password"
                ).permitAll()
                .requestMatchers(
                        "/update",
                        "/whoami"
                ).authenticated()
                .anyRequest().authenticated()

        );


        log.info("Configurando filtro de autenticación básica");

        http.httpBasic(basic -> basic.authenticationEntryPoint((request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(ErrorResponse.jsonOf("No autorizado", HttpStatus.UNAUTHORIZED));
        }));


        log.info("Configurando autenticación");
        http.sessionManagement(sesion -> sesion
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        log.info("Configurando filtro de autenticación JWT");
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.userDetailsService(securityUserDetailsService);


        log.info("Configurando filtro de autenticación básica");
        http.httpBasic(basic -> basic
                .authenticationEntryPoint((request, response, authException) -> response
                        .sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()))
        );

        return http.build();
    }

    public CorsConfigurationSource corsConfigurationSource() {

        var source = new UrlBasedCorsConfigurationSource();
        var configuration = new CorsConfiguration();

        if (environmentChecker.isProduction()) {
            log.info("Configurando origen cruzado para producción");
            configuration.setAllowedOrigins(List.of("https://localhost:80"));
        } else {
            log.info("Configurando origen cruzado para desarrollo");
            configuration.setAllowedOrigins(List.of("*"));
        }

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }


    @Bean
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        var authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(securityUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }
}
