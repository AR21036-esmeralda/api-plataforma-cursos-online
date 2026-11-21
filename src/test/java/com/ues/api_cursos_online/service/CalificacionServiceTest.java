package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CalificacionRequestDTO;
import com.ues.api_cursos_online.dto.CalificacionResponseDTO;
import com.ues.api_cursos_online.entity.Calificacion;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CalificacionRepository;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private CalificacionService calificacionService;

    private Usuario estudiante;
    private Evaluacion evaluacion;
    private Calificacion calificacion;
    private CalificacionRequestDTO request;

    @BeforeEach
    void setUp() {
        Rol rolEstudiante = new Rol();
        rolEstudiante.setNombreRol("ESTUDIANTE");

        estudiante = new Usuario();
        estudiante.setIdUsuario(4L);
        estudiante.setNombreUsuario("Lucía");
        estudiante.setApellidoUsuario("Fernández");
        estudiante.setEstado(true);
        estudiante.setRol(rolEstudiante);

        evaluacion = Evaluacion.builder().idEvaluacion(1L).titulo("Evaluación Final").build();

        calificacion = Calificacion.builder()
                .idCalificacion(20L).usuario(estudiante).evaluacion(evaluacion)
                .nota(new BigDecimal("9.50")).fechaRealizacion(LocalDateTime.now()).build();

        request = CalificacionRequestDTO.builder()
                .idUsuario(4L).idEvaluacion(1L).nota(new BigDecimal("9.50")).build();
    }

    @Test
    void crear_conDatosValidos_guardaConFecha() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(calificacionRepository.existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 1L)).thenReturn(false);
        when(calificacionRepository.save(any(Calificacion.class))).thenAnswer(inv -> inv.getArgument(0));

        CalificacionResponseDTO respuesta = calificacionService.crear(request);

        assertEquals(new BigDecimal("9.50"), respuesta.getNota());
        assertEquals(4L, respuesta.getIdUsuario());
        assertEquals("Evaluación Final", respuesta.getTituloEvaluacion());
        assertNotNull(respuesta.getFechaRealizacion());
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
    void crear_conCalificacionDuplicada_lanzaReglaDeNegocio() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(calificacionRepository.existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacion(4L, 1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> calificacionService.crear(request));
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
    void actualizar_cambiaLaNota() {
        when(calificacionRepository.findById(20L)).thenReturn(Optional.of(calificacion));
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(usuarioRepository.findById(4L)).thenReturn(Optional.of(estudiante));
        when(calificacionRepository.existsByUsuario_IdUsuarioAndEvaluacion_IdEvaluacionAndIdCalificacionNot(4L, 1L, 20L))
                .thenReturn(false);
        when(calificacionRepository.save(any(Calificacion.class))).thenAnswer(inv -> inv.getArgument(0));
        request.setNota(new BigDecimal("8.00"));

        CalificacionResponseDTO respuesta = calificacionService.actualizar(20L, request);

        assertEquals(new BigDecimal("8.00"), respuesta.getNota());
    }

    @Test
    void eliminar_existente_laBorra() {
        when(calificacionRepository.findById(20L)).thenReturn(Optional.of(calificacion));

        calificacionService.eliminar(20L);

        verify(calificacionRepository).delete(calificacion);
    }
}
