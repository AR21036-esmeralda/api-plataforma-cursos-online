package com.ues.api_cursos_online.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Se lanza cuando un recurso solicitado no existe (HTTP 404). */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " con id " + id + " no fue encontrado");
    }
}
