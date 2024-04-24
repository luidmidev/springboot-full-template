package com.luidmidev.template.spring.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Objeto de respuesta para errores de cliente
 */
@Data
@AllArgsConstructor
public class ErrorResponse {

    private final String message;
    private final Integer status;

    @JsonInclude(Include.NON_NULL)
    private final Object target;


    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Crear un objeto de respuesta para errores de cliente a partir de un mensaje
     *
     * @param message Mensaje de error
     */
    public ErrorResponse(String message) {
        this.message = message;
        this.status = 400;
        this.target = null;
    }

    /**
     * Crear un objeto de respuesta para errores de cliente a partir de un mensaje y un objetivo
     *
     * @param message Mensaje de error
     */
    public static ErrorResponse of(String message) {
        return new ErrorResponse(message);
    }

    /**
     * Crear un objeto de respuesta para errores de cliente a partir de un mensaje y un objetivo
     *
     * @param message Mensaje de error
     * @param target  Objetivo del error
     */
    public static ErrorResponse of(String message, Object target) {
        return new ErrorResponse(message, 400, target);
    }

    public static ErrorResponse of(String message, Integer status) {
        return new ErrorResponse(message, status, null);
    }

    public static ErrorResponse of(String message, Integer status, Object target) {
        return new ErrorResponse(message, status, target);
    }


    /**
     * Crear un objeto de respuesta en formato JSON para errores de cliente a partir de un mensaje
     *
     * @param message Mensaje de error
     * @return Objeto de respuesta para errores de cliente
     * @throws JsonProcessingException si ocurre un error al procesar el JSON
     */
    public static String jsonOf(String message) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(of(message));
    }

    /**
     * Crear un objeto de respuesta en formato JSON para errores de cliente a partir de un mensaje y un objetivo
     *
     * @param message Mensaje de error
     * @param target  Objetivo del error
     * @return Objeto de respuesta para errores de cliente
     * @throws JsonProcessingException si ocurre un error al procesar el JSON
     */
    public static String jsonOf(String message, Object target) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(of(message, target));
    }


}
