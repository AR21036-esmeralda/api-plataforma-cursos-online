package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Inscripcion;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionServiceTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private InscripcionService inscripcionService;

    private Usuario usuario;
    private Curso curso;
    private Inscripcion inscripcion;
    private InscripcionRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombreUsuario("Javier");
        usuario.setApellidoUsuario("Vasquez");
        usuario.setEmail("javier@test.com");
        usuario.setEstado(true);

        curso = new Curso();
        curso.setIdCurso(1L);
        curso.setNombreCurso("Bases de Datos");
        curso.setDescripcion("Curso de prueba");
        curso.setEstado(true);

        inscripcion = Inscripcion.builder()
                .idInscripcion(1L)
                .usuario(usuario)
                .curso(curso)
                .fechaInscripcion(LocalDateTime.now())
                .estado(true)
                .build();

        requestDTO = new InscripcionRequestDTO();
        requestDTO.setIdUsuario(1L);
        requestDTO.setIdCurso(1L);
    }

    // =========================================================
    // LISTAR
    // =========================================================

    @Test
    void listar_DebeRetornarInscripciones() {

        when(inscripcionRepository.findAll())
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado =
                inscripcionService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        assertEquals(
                1L,
                resultado.get(0).getIdInscripcion()
        );

        assertEquals(
                "Javier",
                resultado.get(0).getNombreUsuario()
        );

        assertEquals(
                "Bases de Datos",
                resultado.get(0).getNombreCurso()
        );

        verify(inscripcionRepository).findAll();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarInscripcion() {

        when(inscripcionRepository.findById(1L))
                .thenReturn(Optional.of(inscripcion));

        InscripcionResponseDTO resultado =
                inscripcionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInscripcion());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdCurso());
        assertEquals("Javier", resultado.getNombreUsuario());
        assertEquals(
                "Bases de Datos",
                resultado.getNombreCurso()
        );

        verify(inscripcionRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeLanzarExcepcion() {

        when(inscripcionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.buscarPorId(99L)
        );

        verify(inscripcionRepository).findById(99L);
    }

    // =========================================================
    // CREAR
    // =========================================================

    @Test
    void crear_ConDatosValidos_DebeCrearInscripcion() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCurso(
                        1L,
                        1L
                ))
                .thenReturn(false);

        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> {

                    Inscripcion nueva =
                            invocation.getArgument(0);

                    nueva.setIdInscripcion(1L);

                    return nueva;
                });

        InscripcionResponseDTO resultado =
                inscripcionService.crear(requestDTO);

        assertNotNull(resultado);

        assertEquals(
                1L,
                resultado.getIdInscripcion()
        );

        assertEquals(
                1L,
                resultado.getIdUsuario()
        );

        assertEquals(
                1L,
                resultado.getIdCurso()
        );

        assertTrue(resultado.getEstado());

        verify(usuarioRepository).findById(1L);
        verify(cursoRepository).findById(1L);

        verify(inscripcionRepository)
                .existsByUsuario_IdUsuarioAndCurso_IdCurso(
                        1L,
                        1L
                );

        verify(inscripcionRepository)
                .save(any(Inscripcion.class));
    }

    @Test
    void crear_UsuarioNoExiste_DebeLanzarExcepcion() {

        when(usuarioRepository.findById(99L))
                .thenReturn(Optional.empty());

        requestDTO.setIdUsuario(99L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(usuarioRepository).findById(99L);

        verify(inscripcionRepository, never())
                .save(any(Inscripcion.class));
    }

    @Test
    void crear_CursoNoExiste_DebeLanzarExcepcion() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(cursoRepository.findById(99L))
                .thenReturn(Optional.empty());

        requestDTO.setIdCurso(99L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(usuarioRepository).findById(1L);
        verify(cursoRepository).findById(99L);

        verify(inscripcionRepository, never())
                .save(any(Inscripcion.class));
    }

    @Test
    void crear_InscripcionDuplicada_DebeLanzarExcepcion() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCurso(
                        1L,
                        1L
                ))
                .thenReturn(true);

        IllegalArgumentException excepcion =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> inscripcionService.crear(requestDTO)
                );

        assertEquals(
                "El usuario ya está inscrito en este curso",
                excepcion.getMessage()
        );

        verify(inscripcionRepository, never())
                .save(any(Inscripcion.class));
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @Test
    void actualizar_ConDatosValidos_DebeActualizarInscripcion() {

        when(inscripcionRepository.findById(1L))
                .thenReturn(Optional.of(inscripcion));

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(cursoRepository.findById(1L))
                .thenReturn(Optional.of(curso));

        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        InscripcionResponseDTO resultado =
                inscripcionService.actualizar(
                        1L,
                        requestDTO
                );

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInscripcion());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdCurso());

        verify(inscripcionRepository).findById(1L);

        verify(inscripcionRepository)
                .save(any(Inscripcion.class));
    }

    @Test
    void actualizar_InscripcionNoExiste_DebeLanzarExcepcion() {

        when(inscripcionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.actualizar(
                        99L,
                        requestDTO
                )
        );

        verify(inscripcionRepository, never())
                .save(any(Inscripcion.class));
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Test
    void eliminar_CuandoExiste_DebeEliminarInscripcion() {

        when(inscripcionRepository.findById(1L))
                .thenReturn(Optional.of(inscripcion));

        inscripcionService.eliminar(1L);

        verify(inscripcionRepository).findById(1L);
        verify(inscripcionRepository).delete(inscripcion);
    }

    @Test
    void eliminar_CuandoNoExiste_DebeLanzarExcepcion() {

        when(inscripcionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.eliminar(99L)
        );

        verify(inscripcionRepository, never())
                .delete(any(Inscripcion.class));
    }

    // =========================================================
    // CURSOS DE UN ESTUDIANTE
    // =========================================================

    @Test
    void listarPorEstudiante_DebeRetornarSusCursos() {

        when(usuarioRepository.existsById(1L))
                .thenReturn(true);

        when(inscripcionRepository
                .findByUsuario_IdUsuario(1L))
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado =
                inscripcionService.listarPorEstudiante(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        assertEquals(
                "Bases de Datos",
                resultado.get(0).getNombreCurso()
        );

        verify(usuarioRepository).existsById(1L);

        verify(inscripcionRepository)
                .findByUsuario_IdUsuario(1L);
    }

    @Test
    void listarPorEstudiante_UsuarioNoExiste_DebeLanzarExcepcion() {

        when(usuarioRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService
                        .listarPorEstudiante(99L)
        );

        verify(
                inscripcionRepository,
                never()
        ).findByUsuario_IdUsuario(anyLong());
    }

    // =========================================================
    // ESTUDIANTES DE UN CURSO
    // =========================================================

    @Test
    void listarPorCurso_DebeRetornarEstudiantesInscritos() {

        when(cursoRepository.existsById(1L))
                .thenReturn(true);

        when(inscripcionRepository
                .findByCurso_IdCurso(1L))
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado =
                inscripcionService.listarPorCurso(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        assertEquals(
                "Javier",
                resultado.get(0).getNombreUsuario()
        );

        verify(cursoRepository).existsById(1L);

        verify(inscripcionRepository)
                .findByCurso_IdCurso(1L);
    }

    @Test
    void listarPorCurso_CursoNoExiste_DebeLanzarExcepcion() {

        when(cursoRepository.existsById(99L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService
                        .listarPorCurso(99L)
        );

        verify(
                inscripcionRepository,
                never()
        ).findByCurso_IdCurso(anyLong());
    }
}