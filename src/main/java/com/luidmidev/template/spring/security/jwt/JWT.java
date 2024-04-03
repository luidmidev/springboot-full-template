package com.luidmidev.template.spring.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.Date;
import java.util.function.Function;

/**
 * Componente para la generación y validación de tokens JWT.
 */
@Log4j2
@Component
public class JWT {

    /**
     * Clave secreta utilizada para firmar los tokens JWT.
     */
    @Value("${security.jwt.secret}")
    private String key;

    /**
     * Emisor del token JWT.
     */
    @Value("${security.jwt.issuer}")
    private String issuer;

    /**
     * Tiempo de vida (en milisegundos) del token JWT.
     */
    @Value("${security.jwt.ttlMillis}")
    private long ttlMillis;

    /**
     * Crea un token JWT con el nombre de usuario y el correo electrónico proporcionados.
     *
     * @param id      Nombre de usuario para incluir en el token.
     * @param subject Correo electrónico para incluir en el token.
     * @return El token JWT generado.
     */
    public String create(String id, String subject) {

        log.info("Creating JWT for user id {} and subject {}", id, subject);


        var nowMillis = System.currentTimeMillis();
        var now = new Date(nowMillis);
        var signingKey = getSigningKey();

        var builder = Jwts.builder()
                .id(id) // ID del token
                .issuedAt(now) // Fecha de creación del token
                .subject(subject) // Asunto del token
                .issuer(issuer) // Emisor del token
                .signWith(signingKey, Jwts.SIG.HS256);  // Firma del token

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.expiration(exp);
        }
        return builder.compact();
    }

    private SecretKey getSigningKey() {
        var apiKeySecretBytes = DatatypeConverter.parseBase64Binary(key); // Clave secreta en bytes
        return new SecretKeySpec(apiKeySecretBytes, "HmacSHA256"); // Clave secreta para firmar el token
    }


    /**
     * Obtiene el nombre de usuario de un token JWT.
     *
     * @param jwt Token JWT del cual se obtiene el nombre de usuario.
     * @return El nombre de usuario del token.
     */
    public String getID(String jwt) {
        log.debug("getting id from jwt '{}'", jwt);
        return getClaim(jwt, Claims::getId);
    }

    /**
     * Obtiene el correo electrónico de un token JWT.
     *
     * @param jwt Token JWT del cual se obtiene el correo electrónico.
     * @return El correo electrónico del token.
     */
    public String getSubject(String jwt) {
        log.debug("getting subject from jwt '{}'", jwt);
        return getClaim(jwt, Claims::getSubject);
    }

    /**
     * Obtiene un reclamo específico del token JWT utilizando un resolvedor de reclamos dado.
     *
     * @param jwt            Token JWT del cual se obtendrá el reclamo.
     * @param claimsResolver Función que resuelve el reclamo deseado a partir de los Claims del token.
     * @param <T>            Tipo de dato del reclamo que se desea obtener.
     * @return El reclamo específico del token JWT.
     */
    private <T> T getClaim(String jwt, Function<Claims, T> claimsResolver) {
        var claims = getAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    /**
     * Obtiene todos los reclamos (Claims) contenidos en el token JWT.
     *
     * @param jwt Token JWT del cual se obtendrán los reclamos.
     * @return Objeto Claims que representa todos los reclamos del token JWT.
     */
    private Claims getAllClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }
}
