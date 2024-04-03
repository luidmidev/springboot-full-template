package com.luidmidev.template.spring.utils;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Utilidad para codificar y verificar contraseñas utilizando el algoritmo de hash Argon2.
 */
public final class Argon2Encoder {
    /**
     * Constructor privado para evitar la creación de instancias de la clase.
     * Lanza un AssertionError si se intenta crear una instancia.
     */
    private Argon2Encoder() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Codifica el texto de la contraseña utilizando el algoritmo Argon2.
     *
     * @param text el texto de la contraseña a codificar.
     * @return la cadena de caracteres resultante de la codificación.
     */
    public static String encode(String text) {
        Argon2 argon = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon.hash(1, 1024, 1, text.toCharArray());
    }

    /**
     * Verifica si el texto coincide con la codificación hash.
     *
     * @param hash el hash de la contraseña a verificar.
     * @param text el texto a verificar.
     * @return true si el texto coincide con el hash, false de lo contrario.
     */
    public static Boolean verify(String hash, String text) {
        Argon2 argon = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon.verify(hash, text.toCharArray());
    }
}
