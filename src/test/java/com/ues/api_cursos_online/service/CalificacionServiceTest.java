package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CalificacionRequestDTO;
import com.ues.api_cursos_online.dto.CalificacionResponseDTO;
import com.ues.api_cursos_online.entity.Calificacion;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CalificacionRepository;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
import com.ues.api_cursos_online.repository.InscripcionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private CalificacionService calificacionService;

    private Usuario estudiante;
    private Usuario profesor;
    private Curso curso;
    private Evaluacion evaluacion;
    private Calificacion calificacion;
    private CalificacionRequestDTO request;

    @BeforeEach
    void setUp() {
        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombreRol("ESTUDIANTE");

        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");

        estudiante = new Usuario();
        estudiante.setIdUsuario(4L);
        estudiante.setNombreUsuario("Lucía");
        estudiante.setApellidoUsuario("Fernández");
        estudiante.setEstado(true);
        estudiante.setRol(rolEstudiante);

        profesor = new Usuario();
        profesor.setIdUsuario(2L);
        profesor.setNombreUsuario("Ana");
        profesor.setApellidoUsuario("Martínez");
        profesor.setEstado(true);
        profesor.setRol(rolProfesor);

        curso = Curso.builder().idCurso(1L).nombreCurso("Desarrollo Web")
                .estado(true).profesor(profesor).build();
        evaluacion = Evaluacion.builder().idEvaluacion(1L).titulo("Evaluación Final")
                .curso(curso).build();

        calificacion = Calificacion.builder()
                .idCalificacion(20L).usuario(estudiante).evaluacion(evaluacion)
                .nota(new BigDecimal("9.50")).fechaRealizacion(LocalDateTime.now()).build();

        request = CalificacionRequestDTO.builder()
                .idUsuario(4L).idEvaluacion(1L).nota(new BigDecimal("9.50")).build();
    }

    private void inscripcionActiva() {
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(true);
    }

    @Test
    void crear_conDatosValidos_guardaConFecha() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(calificacionRepository
                .existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 1L)).thenReturn(false);
        when(calificacionRepository.save(any(Calificacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CalificacionResponseDTO respuesta = calificacionService.crear(request);

        assertEquals(new BigDecimal("9.50"), respuesta.getNota());
        assertEquals(4L, respuesta.getIdUsuario());
        assertEquals("Evaluación Final", respuesta.getTituloEvaluacion());
        assertNotNull(respuesta.getFechaRealizacion());
        verify(inscripcionRepository)
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L);
    }

    @Test
    void crear_conEvaluacionInexistente_lanzaNotFound() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> calificacionService.crear(request));
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void crear_conUsuarioInexistente_lanzaNotFound() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> calificacionService.crear(request));
    }

    @Test
    void crear_conUsuarioQueNoEsEstudiante_lanzaReglaDeNegocio() {
        Rol rolProfesor = new Rol();
        rolProfesor.setNombreRol("PROFESOR");
        estudiante.setRol(rolProfesor);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));

        assertThrows(BusinessRuleException.class, () -> calificacionService.crear(request));
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void crear_conEstudianteInactivo_lanzaReglaDeNegocio() {
        estudiante.setEstado(false);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));

        assertThrows(BusinessRuleException.class, () -> calificacionService.crear(request));
    }

    @Test
    void crear_sinInscripcionActiva_lanzaReglaDeNegocio() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(false);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> calificacionService.crear(request)
        );

        assertEquals("El estudiante no está inscrito en el curso de la evaluación",
                excepcion.getMessage());
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void crear_conProfesorQueImparteElCurso_guardaCalificacion() {
        request.setIdProfesor(2L);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));
        when(calificacionRepository
                .existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 1L)).thenReturn(false);
        when(calificacionRepository.save(any(Calificacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CalificacionResponseDTO respuesta = calificacionService.crear(request);

        assertNotNull(respuesta);
        verify(usuarioRepository).findById(2L);
    }

    @Test
    void crear_conProfesorQueNoImparteElCurso_lanzaReglaDeNegocio() {
        Usuario otroProfesor = new Usuario();
        otroProfesor.setIdUsuario(9L);
        otroProfesor.setEstado(true);
        otroProfesor.setRol(profesor.getRol());

        request.setIdProfesor(9L);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(usuarioRepository.findById(9L)).thenReturn(Optional.of(otroProfesor));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> calificacionService.crear(request)
        );

        assertEquals("El profesor no imparte el curso de la evaluación", excepcion.getMessage());
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void crear_conProfesorInactivo_lanzaReglaDeNegocio() {
        profesor.setEstado(false);
        request.setIdProfesor(2L);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(profesor));

        assertThrows(BusinessRuleException.class, () -> calificacionService.crear(request));
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void crear_conProfesorConRolInvalido_lanzaReglaDeNegocio() {
        request.setIdProfesor(4L);
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();

        assertThrows(BusinessRuleException.class, () -> calificacionService.crear(request));
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void crear_conCalificacionDuplicada_lanzaReglaDeNegocio() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(calificacionRepository
                .existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 1L)).thenReturn(true);

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> calificacionService.crear(request)
        );

        assertEquals("El estudiante ya tiene una calificación en esta evaluación",
                excepcion.getMessage());
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void obtenerPorId_inexistente_lanzaNotFound() {
        when(calificacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> calificacionService.obtenerPorId(99L));
    }

    @Test
    void listar_sinFiltros_devuelveTodas() {
        when(calificacionRepository.buscar(null, null)).thenReturn(List.of(calificacion));

        List<CalificacionResponseDTO> lista = calificacionService.listar(null, null);

        assertEquals(1, lista.size());
        assertEquals("Lucía Fernández", lista.get(0).getNombreEstudiante());
    }

    @Test
    void actualizar_cambiaLaNotaYLaFecha() {
        LocalDateTime fechaAnterior = LocalDateTime.now().minusDays(5);
        calificacion.setFechaRealizacion(fechaAnterior);

        when(calificacionRepository.findById(20L)).thenReturn(Optional.of(calificacion));
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        inscripcionActiva();
        when(calificacionRepository
                .existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacionAndIdCalificacionNot(
                        4L, 1L, 20L)).thenReturn(false);
        when(calificacionRepository.save(any(Calificacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        request.setNota(new BigDecimal("8.00"));

        CalificacionResponseDTO respuesta = calificacionService.actualizar(20L, request);

        assertEquals(new BigDecimal("8.00"), respuesta.getNota());
        ArgumentCaptor<Calificacion> captor = ArgumentCaptor.forClass(Calificacion.class);
        verify(calificacionRepository).save(captor.capture());
        assertTrue(captor.getValue().getFechaRealizacion().isAfter(fechaAnterior));
    }

    @Test
    void actualizar_sinInscripcionActiva_lanzaReglaDeNegocio() {
        when(calificacionRepository.findById(20L)).thenReturn(Optional.of(calificacion));
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(inscripcionRepository
                .existsByUsuario_IdUsuarioAndCurso_IdCursoAndEstadoTrue(4L, 1L))
                .thenReturn(false);

        assertThrows(BusinessRuleException.class,
                () -> calificacionService.actualizar(20L, request));
        verify(calificacionRepository, never()).save(any());
    }

    @Test
    void eliminar_existente_laBorra() {
        when(calificacionRepository.findById(20L)).thenReturn(Optional.of(calificacion));

        calificacionService.eliminar(20L);

        verify(calificacionRepository).delete(calificacion);
    }
}
