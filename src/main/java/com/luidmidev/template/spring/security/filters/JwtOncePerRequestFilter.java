package com.luidmidev.template.spring.security.filters;

import com.luidmidev.template.spring.exceptions.ErrorResponse;
import com.luidmidev.template.spring.security.UserDetailsAuthenticaction;
import com.luidmidev.template.spring.security.jwt.Jwt;
import com.luidmidev.template.spring.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que se ejecuta una vez por cada solicitud HTTP para validar y procesar el token JWT en el encabezado de autorización.
 * Si el token es válido, establece la autenticación en el contexto de seguridad de Spring Security.
 */
@Log4j2
@Component
public class JwtOncePerRequestFilter extends OncePerRequestFilter {
    private final Jwt jwt;
    private final UserService userDetailsService;


    /**
     * Instantiates a new Jwt once per request filter.
     *
     * @param jwt            the jwt util
     * @param userDetailsService the user details service
     */
    public JwtOncePerRequestFilter(Jwt jwt, UserService userDetailsService) {
        this.jwt = jwt;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Metodo que se ejecuta para cada solicitud HTTP y realiza la validación y procesamiento del token JWT.
     *
     * @param request     Objeto HttpServletRequest que representa la solicitud HTTP actual.
     * @param response    Objeto HttpServletResponse que representa la respuesta HTTP actual.
     * @param filterChain Objeto FilterChain utilizado para invocar el siguiente filtro en la cadena de filtros.
     * @throws ServletException Si ocurre una excepción relacionada con el servlet.
     * @throws IOException      Si ocurre una excepción de E/S.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String authenticationHeader = request.getHeader("Authorization");

        if (authenticationHeader == null || !authenticationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authenticationHeader.replace("Bearer ", "");

        try {

            String username = jwt.getSubject(jwtToken);
            UserDetails user = userDetailsService.loadUserByUsername(username);
            var authentication = new UserDetailsAuthenticaction(user);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);


        } catch (UnsupportedJwtException err) {
            writeError(HttpServletResponse.SC_UNAUTHORIZED, "Token de autenticación no soportado, inicie sesion nuevamente", response);
        } catch (MalformedJwtException err) {
            writeError(HttpServletResponse.SC_UNAUTHORIZED, "Token de autenticación inválido, inicie sesion nuevamente", response);
        } catch (ExpiredJwtException err) {
            writeError(HttpServletResponse.SC_CONFLICT, "Sesión expirada. Inicie sesión nuevamente", response);
        } catch (UsernameNotFoundException err) {
            writeError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado", response);
        } catch (IllegalArgumentException | DataAccessResourceFailureException err) {
            writeError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error en el servidor: " + err.getMessage(), response);
        }
    }


    private void writeError(Integer code, String message, HttpServletResponse response) throws IOException {
        String json = ErrorResponse.jsonOf(message);
        response.setStatus(code);
        response.getWriter().write(json);
    }
}
