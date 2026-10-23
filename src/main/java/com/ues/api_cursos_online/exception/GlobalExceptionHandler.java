
package com.ues.api_cursos_online.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

      @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> manejarRecursoNoEncontrado(
            ResourceNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "mensaje", ex.getMessage(),
                        "error", "Recurso no encontrado"
                ));
    }

       @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, String>> manejarReglaDeNegocio(
            BusinessRuleException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "mensaje", ex.getMessage(),
                        "error", "Regla de negocio incumplida"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarDatosInvalidos(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatearErrorCampo)
                .distinct()
                .collect(Collectors.joining("; "));

        if (mensaje.isBlank()) {
            mensaje = "Los datos enviados no son válidos";
        }

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje", mensaje,
                        "error", "Datos inválidos"
                ));
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> manejarRestricciones(
            ConstraintViolationException ex) {

        String mensaje = ex.getConstraintViolations()
                .stream()
                .map(violacion -> violacion.getMessage())
                .distinct()
                .collect(Collectors.joining("; "));

        if (mensaje.isBlank()) {
            mensaje = "Uno o más parámetros no son válidos";
        }

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje", mensaje,
                        "error", "Parámetro inválido"
                ));
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarTipoInvalido(
            MethodArgumentTypeMismatchException ex) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje", "El parámetro '" + ex.getName()
                                + "' tiene un formato inválido",
                        "error", "Parámetro inválido"
                ));
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarJsonInvalido(
            HttpMessageNotReadableException ex) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje", "El cuerpo de la solicitud está vacío, "
                                + "mal formado o contiene tipos de datos inválidos",
                        "error", "Solicitud inválida"
                ));
    }


    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> manejarParametroFaltante(
            MissingServletRequestParameterException ex) {

        return ResponseEntity.badRequest()
                .body(Map.of(
                        "mensaje", "Falta el parámetro obligatorio: "
                                + ex.getParameterName(),
                        "error", "Parámetro faltante"
                ));
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarIntegridad(
            DataIntegrityViolationException ex) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "mensaje", "La operación no se pudo realizar "
                                + "porque viola una restricción de la base de datos",
                        "error", "Conflicto de integridad"
                ));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarErrorInterno(
            Exception ex) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "mensaje", "Ocurrió un problema al procesar la solicitud",
                        "error", "Error interno del servidor"
                ));
    }


    private String formatearErrorCampo(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}
