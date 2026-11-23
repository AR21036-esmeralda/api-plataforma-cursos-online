package com.ues.api_cursos_online.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.service.InscripcionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class InscripcionControllerTest {

    @Mock
    private InscripcionService inscripcionService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private InscripcionResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        InscripcionController controller =
                new InscripcionController(inscripcionService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        responseDTO = new InscripcionResponseDTO();

        responseDTO.setIdInscripcion(1L);

        responseDTO.setIdUsuario(1L);
        responseDTO.setNombreUsuario("Javier");
        responseDTO.setApellidoUsuario("Vasquez");

        responseDTO.setIdCurso(1L);
        responseDTO.setNombreCurso("Bases de Datos");

        responseDTO.setFechaInscripcion(
                LocalDateTime.of(
                        2026,
                        11,
                        22,
                        20,
                        30
                )
        );

        responseDTO.setEstado(true);
    }

    // =========================================================
    // GET - LISTAR
    // =========================================================

    @Test
    void listar_DebeRetornar200YListaDeInscripciones()
            throws Exception {

        when(inscripcionService.listar())
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get("/api/inscripciones")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        ))
                .andExpect(jsonPath("$[0].idInscripcion")
                        .value(1))
                .andExpect(jsonPath("$[0].idUsuario")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreUsuario")
                        .value("Javier"))
                .andExpect(jsonPath("$[0].idCurso")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreCurso")
                        .value("Bases de Datos"))
                .andExpect(jsonPath("$[0].estado")
                        .value(true));

        verify(inscripcionService).listar();
    }

    // =========================================================
    // GET - BUSCAR POR ID
    // =========================================================

    @Test
    void buscarPorId_DebeRetornar200EInscripcion()
            throws Exception {

        when(inscripcionService.buscarPorId(1L))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        get("/api/inscripciones/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInscripcion")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.nombreUsuario")
                        .value("Javier"))
                .andExpect(jsonPath("$.idCurso")
                        .value(1))
                .andExpect(jsonPath("$.nombreCurso")
                        .value("Bases de Datos"))
                .andExpect(jsonPath("$.estado")
                        .value(true));

        verify(inscripcionService).buscarPorId(1L);
    }

    // =========================================================
    // POST - CREAR
    // =========================================================

    @Test
    void crear_ConDatosValidos_DebeRetornar201()
            throws Exception {

        InscripcionRequestDTO request =
                new InscripcionRequestDTO();

        request.setIdUsuario(1L);
        request.setIdCurso(1L);

        when(inscripcionService.crear(
                any(InscripcionRequestDTO.class)
        )).thenReturn(responseDTO);

        mockMvc.perform(
                        post("/api/inscripciones")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idInscripcion")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.idCurso")
                        .value(1))
                .andExpect(jsonPath("$.estado")
                        .value(true));

        verify(inscripcionService)
                .crear(any(InscripcionRequestDTO.class));
    }

    // =========================================================
    // POST - VALIDACIÓN
    // =========================================================

    @Test
    void crear_SinIdUsuario_DebeRetornar400()
            throws Exception {

        InscripcionRequestDTO request =
                new InscripcionRequestDTO();

        request.setIdCurso(1L);

        mockMvc.perform(
                        post("/api/inscripciones")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(
                inscripcionService,
                never()
        ).crear(any(InscripcionRequestDTO.class));
    }

    @Test
    void crear_SinIdCurso_DebeRetornar400()
            throws Exception {

        InscripcionRequestDTO request =
                new InscripcionRequestDTO();

        request.setIdUsuario(1L);

        mockMvc.perform(
                        post("/api/inscripciones")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isBadRequest());

        verify(
                inscripcionService,
                never()
        ).crear(any(InscripcionRequestDTO.class));
    }

    // =========================================================
    // PUT - ACTUALIZAR
    // =========================================================

    @Test
    void actualizar_ConDatosValidos_DebeRetornar200()
            throws Exception {

        InscripcionRequestDTO request =
                new InscripcionRequestDTO();

        request.setIdUsuario(1L);
        request.setIdCurso(1L);

        when(inscripcionService.actualizar(
                eq(1L),
                any(InscripcionRequestDTO.class)
        )).thenReturn(responseDTO);

        mockMvc.perform(
                        put("/api/inscripciones/{id}", 1L)
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInscripcion")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.idCurso")
                        .value(1));

        verify(inscripcionService)
                .actualizar(
                        eq(1L),
                        any(InscripcionRequestDTO.class)
                );
    }

    // =========================================================
    // DELETE - ELIMINAR
    // =========================================================

    @Test
    void eliminar_DebeRetornar204()
            throws Exception {

        doNothing()
                .when(inscripcionService)
                .eliminar(1L);

        mockMvc.perform(
                        delete("/api/inscripciones/{id}", 1L)
                )
                .andExpect(status().isNoContent());

        verify(inscripcionService).eliminar(1L);
    }

    // =========================================================
    // PATCH - BAJA Y REACTIVACION
    // =========================================================

    @Test
    void darDeBaja_DebeRetornarInscripcionInactiva()
            throws Exception {

        responseDTO.setEstado(false);
        when(inscripcionService.darDeBaja(1L)).thenReturn(responseDTO);

        mockMvc.perform(
                        patch("/api/inscripciones/{id}/baja", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInscripcion").value(1))
                .andExpect(jsonPath("$.estado").value(false));

        verify(inscripcionService).darDeBaja(1L);
    }

    @Test
    void reactivar_DebeRetornarInscripcionActiva()
            throws Exception {

        when(inscripcionService.reactivar(1L)).thenReturn(responseDTO);

        mockMvc.perform(
                        patch("/api/inscripciones/{id}/reactivar", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idInscripcion").value(1))
                .andExpect(jsonPath("$.estado").value(true));

        verify(inscripcionService).reactivar(1L);
    }

    // =========================================================
    // GET - CURSOS DE UN ESTUDIANTE
    // =========================================================

    @Test
    void listarCursosDeEstudiante_DebeRetornar200()
            throws Exception {

        when(inscripcionService.listarPorEstudiante(1L, false))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get(
                                "/api/inscripciones/estudiante/{idUsuario}/cursos",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario")
                        .value(1))
                .andExpect(jsonPath("$[0].idCurso")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreCurso")
                        .value("Bases de Datos"));

        verify(inscripcionService)
                .listarPorEstudiante(1L, false);
    }

    @Test
    void listarCursosDeEstudiante_ConIncluirInactivas_DebeRetornar200()
            throws Exception {

        when(inscripcionService.listarPorEstudiante(1L, true))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get("/api/inscripciones/estudiante/{idUsuario}/cursos", 1L)
                                .param("incluirInactivas", "true")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1));

        verify(inscripcionService).listarPorEstudiante(1L, true);
    }

    // =========================================================
    // GET - ESTUDIANTES DE UN CURSO
    // =========================================================

    @Test
    void listarEstudiantesDeCurso_DebeRetornar200()
            throws Exception {

        when(inscripcionService.listarPorCurso(1L, false))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get(
                                "/api/inscripciones/curso/{idCurso}/estudiantes",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCurso")
                        .value(1))
                .andExpect(jsonPath("$[0].idUsuario")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreUsuario")
                        .value("Javier"));

        verify(inscripcionService)
                .listarPorCurso(1L, false);
    }

    @Test
    void listarEstudiantesDeCurso_ConIncluirInactivas_DebeRetornar200()
            throws Exception {

        when(inscripcionService.listarPorCurso(1L, true))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get("/api/inscripciones/curso/{idCurso}/estudiantes", 1L)
                                .param("incluirInactivas", "true")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCurso").value(1));

        verify(inscripcionService).listarPorCurso(1L, true);
    }
}