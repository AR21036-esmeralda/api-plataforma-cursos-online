package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.ProgresoRequestDTO;
import com.ues.api_cursos_online.dto.ProgresoResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.entity.Progreso;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.LeccionRepository;
import com.ues.api_cursos_online.repository.ProgresoRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgresoServiceTest {

    @Mock
    private ProgresoRepository progresoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private ProgresoService progresoService;

    private Usuario usuario;
    private Curso curso;
    private Leccion leccion;
    private Progreso progreso;
    private ProgresoRequestDTO requestDTO;

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

        leccion = new Leccion();
        leccion.setIdLeccion(1L);
        leccion.setTitulo("Introduccion");
        leccion.setContenido("Contenido de prueba");
        leccion.setOrden(1);
        leccion.setCurso(curso);

        progreso = Progreso.builder()
                .idProgreso(1L)
                .usuario(usuario)
                .leccion(leccion)
                .estado(false)
                .fechaCompletada(null)
                .build();

        requestDTO = new ProgresoRequestDTO();
        requestDTO.setIdUsuario(1L);
        requestDTO.setIdLeccion(1L);
        requestDTO.setEstado(false);
    }

    private void inscripcionActiva() {
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L))
                .thenReturn(true);
    }

    // =========================================================
    // LISTAR
    // =========================================================

    @Test
    void listar_DebeRetornarProgresos() {
        when(progresoRepository.findAll()).thenReturn(List.of(progreso));

        List<ProgresoResponseDTO> resultado = progresoService.listar();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdProgreso());
        assertEquals("Javier", resultado.get(0).getNombreUsuario());
        assertEquals("Introduccion", resultado.get(0).getTituloLeccion());
        assertEquals("Bases de Datos", resultado.get(0).getNombreCurso());

        verify(progresoRepository).findAll();
    }

    // =========================================================
    // BUSCAR POR ID
    // =========================================================

    @Test
    void buscarPorId_CuandoExiste_DebeRetornarProgreso() {
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        ProgresoResponseDTO resultado = progresoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProgreso());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdLeccion());

        verify(progresoRepository).findById(1L);
    }

    @Test
    void buscarPorId_CuandoNoExiste_DebeLanzarExcepcion() {
        when(progresoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progresoService.buscarPorId(99L));

        verify(progresoRepository).findById(99L);
    }

    // =========================================================
    // CREAR PENDIENTE
    // =========================================================

    @Test
    void crear_ComoPendiente_DebeGuardarSinFecha() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        inscripcionActiva();
        when(progresoRepository
                .existsByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 1L))
                .thenReturn(false);
        when(progresoRepository.save(any(Progreso.class)))
                .thenAnswer(invocation -> {
                    Progreso nuevo = invocation.getArgument(0);
                    nuevo.setIdProgreso(1L);
                    return nuevo;
                });

        ProgresoResponseDTO resultado = progresoService.crear(requestDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProgreso());
        assertFalse(resultado.getEstado());
        assertNull(resultado.getFechaCompletada());

        verify(usuarioRepository).findById(1L);
        verify(leccionRepository).findById(1L);
        verify(inscripcionRepository)
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L);
        verify(progresoRepository)
                .existsByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 1L);
        verify(progresoRepository).save(any(Progreso.class));
    }

    // =========================================================
    // CREAR COMPLETADO
    // =========================================================

    @Test
    void crear_ComoCompletado_DebeGenerarFecha() {
        requestDTO.setEstado(true);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        inscripcionActiva();
        when(progresoRepository
                .existsByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 1L))
                .thenReturn(false);
        when(progresoRepository.save(any(Progreso.class)))
                .thenAnswer(invocation -> {
                    Progreso nuevo = invocation.getArgument(0);
                    nuevo.setIdProgreso(1L);
                    return nuevo;
                });

        ProgresoResponseDTO resultado = progresoService.crear(requestDTO);

        assertTrue(resultado.getEstado());
        assertNotNull(resultado.getFechaCompletada());

        verify(progresoRepository).save(any(Progreso.class));
    }

    // =========================================================
    // USUARIO NO EXISTE
    // =========================================================

    @Test
    void crear_UsuarioNoExiste_DebeLanzarExcepcion() {
        requestDTO.setIdUsuario(99L);
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progresoService.crear(requestDTO));

        verify(usuarioRepository).findById(99L);
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    // =========================================================
    // LECCION NO EXISTE
    // =========================================================

    @Test
    void crear_LeccionNoExiste_DebeLanzarExcepcion() {
        requestDTO.setIdLeccion(99L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progresoService.crear(requestDTO));

        verify(leccionRepository).findById(99L);
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    // =========================================================
    // INSCRIPCION
    // =========================================================

    @Test
    void crear_SinInscripcionActiva_DebeLanzarReglaDeNegocio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L))
                .thenReturn(false);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> progresoService.crear(requestDTO)
        );

        assertEquals("El usuario no está inscrito en el curso de la lección",
                excepcion.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    // =========================================================
    // DUPLICADO
    // =========================================================

    @Test
    void crear_Duplicado_DebeLanzarReglaDeNegocio() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        inscripcionActiva();
        when(progresoRepository
                .existsByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 1L))
                .thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> progresoService.crear(requestDTO)
        );

        assertEquals("El usuario ya tiene un registro de progreso para esta lección",
                excepcion.getMessage());
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    // =========================================================
    // ACTUALIZAR: PENDIENTE -> COMPLETADA
    // =========================================================

    @Test
    void actualizar_DePendienteACompletado_DebeGenerarFecha() {
        requestDTO.setEstado(true);

        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        inscripcionActiva();
        when(progresoRepository.save(any(Progreso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProgresoResponseDTO resultado = progresoService.actualizar(1L, requestDTO);

        assertTrue(resultado.getEstado());
        assertNotNull(resultado.getFechaCompletada());

        verify(progresoRepository).save(progreso);
    }

    // =========================================================
    // ACTUALIZAR: COMPLETADA -> PENDIENTE
    // =========================================================

    @Test
    void actualizar_DeCompletadoAPendiente_DebeEliminarFecha() {
        progreso.setEstado(true);
        progreso.setFechaCompletada(LocalDateTime.now());
        requestDTO.setEstado(false);

        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        inscripcionActiva();
        when(progresoRepository.save(any(Progreso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProgresoResponseDTO resultado = progresoService.actualizar(1L, requestDTO);

        assertFalse(resultado.getEstado());
        assertNull(resultado.getFechaCompletada());

        verify(progresoRepository).save(progreso);
    }

    @Test
    void actualizar_SinInscripcionActiva_DebeLanzarReglaDeNegocio() {
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(leccionRepository.findById(1L)).thenReturn(Optional.of(leccion));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(1L, 1L))
                .thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> progresoService.actualizar(1L, requestDTO));
        verify(progresoRepository, never()).save(any(Progreso.class));
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    @Test
    void eliminar_CuandoExiste_DebeEliminar() {
        when(progresoRepository.findById(1L)).thenReturn(Optional.of(progreso));

        progresoService.eliminar(1L);

        verify(progresoRepository).findById(1L);
        verify(progresoRepository).delete(progreso);
    }

    @Test
    void eliminar_CuandoNoExiste_DebeLanzarExcepcion() {
        when(progresoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progresoService.eliminar(99L));

        verify(progresoRepository, never()).delete(any(Progreso.class));
    }

    // =========================================================
    // PROGRESO POR ESTUDIANTE
    // =========================================================

    @Test
    void listarPorEstudiante_DebeRetornarProgresos() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(progresoRepository.findByUsuario_IdUsuario(1L)).thenReturn(List.of(progreso));

        List<ProgresoResponseDTO> resultado = progresoService.listarPorEstudiante(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdUsuario());

        verify(progresoRepository).findByUsuario_IdUsuario(1L);
    }

    @Test
    void listarPorEstudiante_UsuarioNoExiste_DebeLanzarExcepcion() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> progresoService.listarPorEstudiante(99L));

        verify(progresoRepository, never()).findByUsuario_IdUsuario(anyLong());
    }

    // =========================================================
    // PROGRESO ESTUDIANTE + CURSO
    // =========================================================

    @Test
    void listarPorEstudianteYCurso_DebeRetornarProgresos() {
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(progresoRepository.findByUsuario_IdUsuarioAndLeccion_Curso_IdCurso(1L, 1L))
                .thenReturn(List.of(progreso));

        List<ProgresoResponseDTO> resultado =
                progresoService.listarPorEstudianteYCurso(1L, 1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getIdCurso());

        verify(progresoRepository)
                .findByUsuario_IdUsuarioAndLeccion_Curso_IdCurso(1L, 1L);
    }

    // =========================================================
    // USUARIO + LECCION
    // =========================================================

    @Test
    void buscarPorUsuarioYLeccion_DebeRetornarProgreso() {
        when(progresoRepository.findByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 1L))
                .thenReturn(Optional.of(progreso));

        ProgresoResponseDTO resultado = progresoService.buscarPorUsuarioYLeccion(1L, 1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdProgreso());
        assertEquals(1L, resultado.getIdUsuario());
        assertEquals(1L, resultado.getIdLeccion());

        verify(progresoRepository).findByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 1L);
    }

    @Test
    void buscarPorUsuarioYLeccion_NoExiste_DebeLanzarExcepcion() {
        when(progresoRepository.findByUsuario_IdUsuarioAndLeccion_IdLeccion(1L, 99L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> progresoService.buscarPorUsuarioYLeccion(1L, 99L));
    }
}
