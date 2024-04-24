package com.luidmidev.template.spring.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class ClientException extends RuntimeException {

    private final transient ErrorResponse errorResponse;

    private final HttpStatus httpStatusCode;

    /**
     * Crear una excepción para errores que se pueden mostrar al cliente a partir de un mensaje
     *
     * @param message Mensaje de error
     */
    public ClientException(String message) {
        super(message);
        errorResponse = ErrorResponse.of(message);
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }


    /**
     * Crear una excepción para errores que se pueden mostrar al cliente a partir de un mensaje y un código de estado HTTP
     *
     * @param message        Mensaje de error
     * @param httpStatusCode Código de estado HTTP
     */
    public ClientException(String message, HttpStatus httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.errorResponse = ErrorResponse.of(message);
    }

    /**
     * Crear una excepción para errores que se pueden mostrar al cliente a partir de un mensaje y un código de estado HTTP
     *
     * @param message        Mensaje de error
     * @param httpStatusCode Código de estado HTTP
     */
    public ClientException(String message, int httpStatusCode) {
        super(message);
        this.httpStatusCode = HttpStatus.valueOf(httpStatusCode);
        this.errorResponse = ErrorResponse.of(message);
    }

    /**
     * Crear una excepción para errores que se pueden mostrar al cliente a partir de un mensaje y un objetivo
     *
     * @param message Mensaje de error
     * @param target  Objetivo del error
     */
    public ClientException(String message, String target) {
        super(message);
        errorResponse = ErrorResponse.of(message, target);
        httpStatusCode = HttpStatus.BAD_REQUEST;
    }

    /**
     * Crear una excepción para errores que se pueden mostrar al cliente a partir de un mensaje, un objetivo y un código de estado HTTP
     *
     * @param message        Mensaje de error
     * @param target         Objetivo del error
     * @param httpStatusCode Código de estado HTTP
     */
    public ClientException(String message, String target, HttpStatus httpStatusCode) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        errorResponse = ErrorResponse.of(message, target);
    }


    /**
     * Crear una excepción para errores que se pueden mostrar al cliente a partir de un mensaje, un objetivo y un código de estado HTTP
     *
     * @param message        Mensaje de error
     * @param target         Objetivo del error
     * @param httpStatusCode Código de estado HTTP
     */
    public ClientException(String message, String target, int httpStatusCode) {
        super(message);
        this.httpStatusCode = HttpStatus.valueOf(httpStatusCode);
        errorResponse = ErrorResponse.of(message, target);
    }
}