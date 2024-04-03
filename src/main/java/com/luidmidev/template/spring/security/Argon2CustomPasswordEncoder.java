package com.luidmidev.template.spring.security;

import com.luidmidev.template.spring.utils.Argon2Encoder;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementación personalizada de PasswordEncoder que utiliza el algoritmo Argon2 para el hash y la verificación de contraseas.
 */
@Log4j2
@Component
public class Argon2CustomPasswordEncoder implements PasswordEncoder {

    /**
     * Codifica la contrasea en bruto utilizando el algoritmo Argon2.
     *
     * @param rawPassword Contrasea en bruto a codificar.
     * @return Contrasea codificada.
     */
    @Override
    public String encode(CharSequence rawPassword) {
        return Argon2Encoder.encode(rawPassword.toString());
    }

    /**
     * Verifica si la contrasea en bruto coincide con la contrasea codificada utilizando el algoritmo Argon2.
     *
     * @param rawPassword     Contrasea en bruto a verificar.
     * @param encodedPassword Contraseña codificada.
     * @return true si la contraseña coincide, false en caso contrario.
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return Argon2Encoder.verify(encodedPassword, rawPassword.toString());
    }

    /**
     * Verifica si la contraseña codificada necesita una actualización o mejora en su algoritmo de codificación.
     * Este método se hereda de la interfaz PasswordEncoder y devuelve false por defecto, lo que indica que no se requiere actualización.
     *
     * @param encodedPassword Contraseña codificada a verificar.
     * @return true si la contraseña necesita actualización, false en caso contrario.
     */
    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        return PasswordEncoder.super.upgradeEncoding(encodedPassword);
    }
}
