package com.ues.api_cursos_online.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ControladorDePrueba())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void businessRuleException_DebeRetornar409() throws Exception {
        mockMvc.perform(get("/prueba/regla-negocio"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Regla incumplida"))
                .andExpect(jsonPath("$.error").value("Regla de negocio incumplida"));
    }

    @Test
    void illegalArgumentException_DebeRetornar409() throws Exception {
        mockMvc.perform(get("/prueba/argumento-invalido"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").value("Argumento inválido"))
                .andExpect(jsonPath("$.error").value("Regla de negocio incumplida"));
    }

    @Test
    void emptyResultDataAccessException_DebeRetornar404() throws Exception {
        mockMvc.perform(get("/prueba/eliminacion-inexistente"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recurso no encontrado"));
    }

    @Test
    void resourceNotFoundException_DebeRetornar404() throws Exception {
        mockMvc.perform(get("/prueba/no-encontrado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Curso con id 99 no fue encontrado"));
    }

    @Test
    void handlerMethodValidationException_DebeRetornar400() throws Exception {
        mockMvc.perform(get("/prueba/parametro-invalido"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Parámetro inválido"));
    }

    @RestController
    static class ControladorDePrueba {

        @GetMapping("/prueba/regla-negocio")
        String reglaNegocio() {
            throw new BusinessRuleException("Regla incumplida");
        }

        @GetMapping("/prueba/argumento-invalido")
        String argumentoInvalido() {
            throw new IllegalArgumentException("Argumento inválido");
        }

        @GetMapping("/prueba/eliminacion-inexistente")
        String eliminacionInexistente() {
            throw new EmptyResultDataAccessException(1);
        }

        @GetMapping("/prueba/no-encontrado")
        String noEncontrado() {
            throw new ResourceNotFoundException("Curso", 99L);
        }

        @GetMapping("/prueba/parametro-invalido")
        String parametroInvalido() {
            throw new HandlerMethodValidationException(mock(MethodValidationResult.class));
        }
    }
}
