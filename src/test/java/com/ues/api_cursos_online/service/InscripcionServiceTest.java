package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.InscripcionRequestDTO;
import com.ues.api_cursos_online.dto.InscripcionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Inscripcion;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombreRol("ESTUDIANTE");

        usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNombreUsuario("Javier");
        usuario.setApellidoUsuario("Vasquez");
        usuario.setEmail("javier@test.com");
        usuario.setEstado(true);
        usuario.setRol(rolEstudiante);

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
        when(inscripcionRepository.findAll()).thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado = inscripcionService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdInscripcion());
        assertEquals("Javier", resultado.get(0).getNombreUsuario());
        assertEquals("Bases de Datos", resultado.get(0).getNombreCurso());

        verify(inscripcionRepository).findAll();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarInscripcion() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));

        InscripcionResponseDTO resultado = inscripcionService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInscripcion());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdCurso());
        assertEquals("Javier", resultado.getNombreUsuario());
        assertEquals("Bases de Datos", resultado.getNombreCurso());

        verify(inscripcionRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

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
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(inscripcionRepository.findByUsuario_IdUsuarioAndCurso_IdCurso(1L, 1L))
                .thenReturn(Optional.empty());
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> {
                    Inscripcion nueva = invocation.getArgument(0);
                    nueva.setIdInscripcion(1L);
                    return nueva;
                });

        InscripcionResponseDTO resultado = inscripcionService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInscripcion());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdCurso());
        assertTrue(resultado.getEstado());
        assertNotNull(resultado.getFechaInscripcion());

        verify(usuarioRepository).findById(1L);
        verify(cursoRepository).findById(1L);
        verify(inscripcionRepository).findByUsuario_IdUsuarioAndCurso_IdCurso(1L, 1L);
        verify(inscripcionRepository).save(any(Inscripcion.class));
    }

    @Test
    void crear_ConInscripcionAnteriorDadaDeBaja_DebeReactivarla() {
        Inscripcion anterior = Inscripcion.builder()
                .idInscripcion(7L)
                .usuario(usuario)
                .curso(curso)
                .fechaInscripcion(LocalDateTime.now().minusMonths(1))
                .estado(false)
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(inscripcionRepository.findByUsuario_IdUsuarioAndCurso_IdCurso(1L, 1L))
                .thenReturn(Optional.of(anterior));
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InscripcionResponseDTO resultado = inscripcionService.crear(requestDTO);

        assertEquals(7L, resultado.getIdInscripcion());
        assertTrue(resultado.getEstado());
        verify(inscripcionRepository).save(anterior);
    }

    @Test
    void crear_InscripcionDuplicadaActiva_DebeLanzarReglaDeNegocio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(inscripcionRepository.findByUsuario_IdUsuarioAndCurso_IdCurso(1L, 1L))
                .thenReturn(Optional.of(inscripcion));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        assertEquals("El usuario ya está inscrito en este curso", excepcion.getMessage());
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void crear_UsuarioNoExiste_DebeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        requestDTO.setIdUsuario(99L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(usuarioRepository).findById(99L);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void crear_UsuarioNoEsEstudiante_DebeLanzarReglaDeNegocio() {
        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");
        usuario.setRol(rolProfesor);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(
                BusinessRuleException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(cursoRepository, never()).findById(anyLong());
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void crear_EstudianteDesactivado_DebeLanzarReglaDeNegocio() {
        usuario.setEstado(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        assertThrows(
                BusinessRuleException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void crear_CursoNoExiste_DebeLanzarExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        requestDTO.setIdCurso(99L);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(usuarioRepository).findById(1L);
        verify(cursoRepository).findById(99L);
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void crear_CursoDesactivado_DebeLanzarReglaDeNegocio() {
        curso.setEstado(false);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        assertThrows(
                BusinessRuleException.class,
                () -> inscripcionService.crear(requestDTO)
        );

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    @Test
    void actualizar_ConDatosValidos_DebeActualizarInscripcion() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InscripcionResponseDTO resultado = inscripcionService.actualizar(1L, requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdInscripcion());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdCurso());

        verify(inscripcionRepository).findById(1L);
        verify(inscripcionRepository).save(any(Inscripcion.class));
    }

    @Test
    void actualizar_InscripcionNoExiste_DebeLanzarExcepcion() {
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.actualizar(99L, requestDTO)
        );

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void actualizar_CuandoYaExisteInscripcionActiva_DebeLanzarReglaDeNegocio() {
        Curso otroCurso = new Curso();
        otroCurso.setIdCurso(2L);
        otroCurso.setEstado(true);

        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(otroCurso));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 2L))
                .thenReturn(true);

        requestDTO.setIdCurso(2L);

        assertThrows(
                BusinessRuleException.class,
                () -> inscripcionService.actualizar(1L, requestDTO)
        );

        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    // =========================================================
    // BAJA Y REACTIVACION
    // =========================================================

    @Test
    void darDeBaja_DebeMarcarLaInscripcionComoInactiva() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InscripcionResponseDTO resultado = inscripcionService.darDeBaja(1L);

        assertFalse(resultado.getEstado());
        ArgumentCaptor<Inscripcion> captor = ArgumentCaptor.forClass(Inscripcion.class);
        verify(inscripcionRepository).save(captor.capture());
        assertFalse(captor.getValue().getEstado());
    }

    @Test
    void darDeBaja_CuandoYaEstabaDadaDeBaja_DebeLanzarReglaDeNegocio() {
        inscripcion.setEstado(false);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> inscripcionService.darDeBaja(1L)
        );

        assertEquals("La inscripción ya estaba dada de baja", excepcion.getMessage());
        verify(inscripcionRepository, never()).save(any(Inscripcion.class));
    }

    @Test
    void reactivar_DebeMarcarLaInscripcionComoActiva() {
        inscripcion.setEstado(false);
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.save(any(Inscripcion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InscripcionResponseDTO resultado = inscripcionService.reactivar(1L);

        assertTrue(resultado.getEstado());
        verify(inscripcionRepository).save(inscripcion);
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Test
    void eliminar_CuandoExiste_DebeEliminarInscripcion() {
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));

        inscripcionService.eliminar(1L);

        verify(inscripcionRepository).findById(1L);
        verify(inscripcionRepository).delete(inscripcion);
    }

    @Test
    void eliminar_CuandoNoExiste_DebeLanzarExcepcion() {
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.eliminar(99L)
        );

        verify(inscripcionRepository, never()).delete(any(Inscripcion.class));
    }

    // =========================================================
    // CURSOS DE UN ESTUDIANTE
    // =========================================================

    @Test
    void listarPorEstudiante_DebeRetornarSusCursosActivos() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(inscripcionRepository.findByUsuario_IdUsuarioAndEstadoTrue(1L))
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado = inscripcionService.listarPorEstudiante(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Bases de Datos", resultado.get(0).getNombreCurso());

        verify(usuarioRepository).existsById(1L);
        verify(inscripcionRepository).findByUsuario_IdUsuarioAndEstadoTrue(1L);
    }

    @Test
    void listarPorEstudiante_ConIncluirInactivas_DebeTraerTodas() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(inscripcionRepository.findByUsuario_IdUsuario(1L))
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado =
                inscripcionService.listarPorEstudiante(1L, true);

        assertEquals(1, resultado.size());
        verify(inscripcionRepository).findByUsuario_IdUsuario(1L);
        verify(inscripcionRepository, never()).findByUsuario_IdUsuarioAndEstadoTrue(anyLong());
    }

    @Test
    void listarPorEstudiante_UsuarioNoExiste_DebeLanzarExcepcion() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.listarPorEstudiante(99L)
        );

        verify(inscripcionRepository, never()).findByUsuario_IdUsuario(anyLong());
        verify(inscripcionRepository, never()).findByUsuario_IdUsuarioAndEstadoTrue(anyLong());
    }

    // =========================================================
    // ESTUDIANTES DE UN CURSO
    // =========================================================

    @Test
    void listarPorCurso_DebeRetornarEstudiantesInscritosActivos() {
        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(inscripcionRepository.findByCurso_IdCursoAndEstadoTrue(1L))
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado = inscripcionService.listarPorCurso(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Javier", resultado.get(0).getNombreUsuario());

        verify(cursoRepository).existsById(1L);
        verify(inscripcionRepository).findByCurso_IdCursoAndEstadoTrue(1L);
    }

    @Test
    void listarPorCurso_ConIncluirInactivas_DebeTraerTodas() {
        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(inscripcionRepository.findByCurso_IdCurso(1L))
                .thenReturn(List.of(inscripcion));

        List<InscripcionResponseDTO> resultado =
                inscripcionService.listarPorCurso(1L, true);

        assertEquals(1, resultado.size());
        verify(inscripcionRepository).findByCurso_IdCurso(1L);
        verify(inscripcionRepository, never()).findByCurso_IdCursoAndEstadoTrue(anyLong());
    }

    @Test
    void listarPorCurso_CursoNoExiste_DebeLanzarExcepcion() {
        when(cursoRepository.existsById(99L)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> inscripcionService.listarPorCurso(99L)
        );

        verify(inscripcionRepository, never()).findByCurso_IdCurso(anyLong());
        verify(inscripcionRepository, never()).findByCurso_IdCursoAndEstadoTrue(anyLong());
    }

    // =========================================================
    // ESTA INSCRITO ACTIVO
    // =========================================================

    @Test
    void estaInscritoActivo_CuandoExisteInscripcionActiva_DebeRetornarTrue() {
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L))
                .thenReturn(true);

        assertTrue(inscripcionService.estaInscritoActivo(1L, 1L));
        verify(inscripcionRepository)
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L);
    }

    @Test
    void estaInscritoActivo_CuandoNoExisteInscripcionActiva_DebeRetornarFalse() {
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L))
                .thenReturn(false);

        assertFalse(inscripcionService.estaInscritoActivo(1L, 1L));
    }
}
