package com.ues.api_cursos_online.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ues.api_cursos_online.dto.ProgresoRequestDTO;
import com.ues.api_cursos_online.dto.ProgresoResponseDTO;
import com.ues.api_cursos_online.service.ProgresoService;
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
class ProgresoControllerTest {

    @Mock
    private ProgresoService progresoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private ProgresoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {

        ProgresoController controller =
                new ProgresoController(progresoService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        responseDTO = new ProgresoResponseDTO();

        responseDTO.setIdProgreso(1L);

        responseDTO.setIdUsuario(1L);
        responseDTO.setNombreUsuario("Javier");
        responseDTO.setApellidoUsuario("Vasquez");

        responseDTO.setIdLeccion(1L);
        responseDTO.setTituloLeccion("Introduccion");

        responseDTO.setIdCurso(1L);
        responseDTO.setNombreCurso("Bases de Datos");

        responseDTO.setEstado(true);

        responseDTO.setFechaCompletada(
                LocalDateTime.of(
                        2026,
                        11,
                        22,
                        20,
                        30
                )
        );
    }

    // =========================================================
    // GET - LISTAR
    // =========================================================

    @Test
    void listar_DebeRetornar200YListaDeProgresos()
            throws Exception {

        when(progresoService.listar())
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get("/api/progresos")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(
                                MediaType.APPLICATION_JSON
                        ))
                .andExpect(jsonPath("$[0].idProgreso")
                        .value(1))
                .andExpect(jsonPath("$[0].idUsuario")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreUsuario")
                        .value("Javier"))
                .andExpect(jsonPath("$[0].idLeccion")
                        .value(1))
                .andExpect(jsonPath("$[0].tituloLeccion")
                        .value("Introduccion"))
                .andExpect(jsonPath("$[0].idCurso")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreCurso")
                        .value("Bases de Datos"))
                .andExpect(jsonPath("$[0].estado")
                        .value(true));

        verify(progresoService).listar();
    }

    // =========================================================
    // GET - BUSCAR POR ID
    // =========================================================

    @Test
    void buscarPorId_DebeRetornar200YProgreso()
            throws Exception {

        when(progresoService.buscarPorId(1L))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        get(
                                "/api/progresos/{id}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProgreso")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.idLeccion")
                        .value(1))
                .andExpect(jsonPath("$.estado")
                        .value(true));

        verify(progresoService)
                .buscarPorId(1L);
    }

    // =========================================================
    // POST - CREAR
    // =========================================================

    @Test
    void crear_ConDatosValidos_DebeRetornar200()
            throws Exception {

        ProgresoRequestDTO request =
                new ProgresoRequestDTO();

        request.setIdUsuario(1L);
        request.setIdLeccion(1L);
        request.setEstado(false);

        when(progresoService.crear(
                any(ProgresoRequestDTO.class)
        )).thenReturn(responseDTO);

        mockMvc.perform(
                        post("/api/progresos")
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
                .andExpect(jsonPath("$.idProgreso")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.idLeccion")
                        .value(1));

        verify(progresoService)
                .crear(any(ProgresoRequestDTO.class));
    }

    // =========================================================
    // POST - VALIDACIONES
    // =========================================================

    @Test
    void crear_SinIdUsuario_DebeRetornar400()
            throws Exception {

        ProgresoRequestDTO request =
                new ProgresoRequestDTO();

        request.setIdLeccion(1L);
        request.setEstado(false);

        mockMvc.perform(
                        post("/api/progresos")
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
                progresoService,
                never()
        ).crear(any(ProgresoRequestDTO.class));
    }

    @Test
    void crear_SinIdLeccion_DebeRetornar400()
            throws Exception {

        ProgresoRequestDTO request =
                new ProgresoRequestDTO();

        request.setIdUsuario(1L);
        request.setEstado(false);

        mockMvc.perform(
                        post("/api/progresos")
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
                progresoService,
                never()
        ).crear(any(ProgresoRequestDTO.class));
    }

    // =========================================================
    // PUT - ACTUALIZAR
    // =========================================================

    @Test
    void actualizar_ConDatosValidos_DebeRetornar200()
            throws Exception {

        ProgresoRequestDTO request =
                new ProgresoRequestDTO();

        request.setIdUsuario(1L);
        request.setIdLeccion(1L);
        request.setEstado(true);

        when(progresoService.actualizar(
                eq(1L),
                any(ProgresoRequestDTO.class)
        )).thenReturn(responseDTO);

        mockMvc.perform(
                        put(
                                "/api/progresos/{id}",
                                1L
                        )
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
                .andExpect(jsonPath("$.idProgreso")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.idLeccion")
                        .value(1))
                .andExpect(jsonPath("$.estado")
                        .value(true));

        verify(progresoService)
                .actualizar(
                        eq(1L),
                        any(ProgresoRequestDTO.class)
                );
    }

    // =========================================================
    // DELETE
    // =========================================================

    @Test
    void eliminar_DebeRetornar200()
            throws Exception {

        doNothing()
                .when(progresoService)
                .eliminar(1L);

        mockMvc.perform(
                        delete(
                                "/api/progresos/{id}",
                                1L
                        )
                )
                .andExpect(status().isOk());

        verify(progresoService)
                .eliminar(1L);
    }

    // =========================================================
    // GET - PROGRESO DE UN ESTUDIANTE
    // =========================================================

    @Test
    void listarPorEstudiante_DebeRetornar200()
            throws Exception {

        when(progresoService
                .listarPorEstudiante(1L))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get(
                                "/api/progresos/estudiante/{idUsuario}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario")
                        .value(1))
                .andExpect(jsonPath("$[0].idLeccion")
                        .value(1))
                .andExpect(jsonPath("$[0].tituloLeccion")
                        .value("Introduccion"));

        verify(progresoService)
                .listarPorEstudiante(1L);
    }

    // =========================================================
    // GET - PROGRESO DE ESTUDIANTE EN UN CURSO
    // =========================================================

    @Test
    void listarPorEstudianteYCurso_DebeRetornar200()
            throws Exception {

        when(progresoService
                .listarPorEstudianteYCurso(
                        1L,
                        1L
                ))
                .thenReturn(List.of(responseDTO));

        mockMvc.perform(
                        get(
                                "/api/progresos/estudiante/{idUsuario}/curso/{idCurso}",
                                1L,
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario")
                        .value(1))
                .andExpect(jsonPath("$[0].idCurso")
                        .value(1))
                .andExpect(jsonPath("$[0].nombreCurso")
                        .value("Bases de Datos"))
                .andExpect(jsonPath("$[0].idLeccion")
                        .value(1));

        verify(progresoService)
                .listarPorEstudianteYCurso(
                        1L,
                        1L
                );
    }

    // =========================================================
    // GET - PROGRESO DE ESTUDIANTE EN UNA LECCION
    // =========================================================

    @Test
    void buscarPorUsuarioYLeccion_DebeRetornar200()
            throws Exception {

        when(progresoService
                .buscarPorUsuarioYLeccion(
                        1L,
                        1L
                ))
                .thenReturn(responseDTO);

        mockMvc.perform(
                        get(
                                "/api/progresos/estudiante/{idUsuario}/leccion/{idLeccion}",
                                1L,
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProgreso")
                        .value(1))
                .andExpect(jsonPath("$.idUsuario")
                        .value(1))
                .andExpect(jsonPath("$.idLeccion")
                        .value(1))
                .andExpect(jsonPath("$.estado")
                        .value(true));

        verify(progresoService)
                .buscarPorUsuarioYLeccion(
                        1L,
                        1L
                );
    }
}