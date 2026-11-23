package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.EvaluacionRequestDTO;
import com.ues.api_cursos_online.dto.EvaluacionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
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
class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private EvaluacionService evaluacionService;

    private Curso curso;
    private Evaluacion evaluacion;
    private EvaluacionRequestDTO request;
    private LocalDateTime fechaLimite;

    @BeforeEach
    void setUp() {
        fechaLimite = LocalDateTime.now().plusDays(7);
        curso = Curso.builder().idCurso(1L).nombreCurso("Desarrollo Web").estado(true).build();
        evaluacion = Evaluacion.builder()
                .idEvaluacion(10L).titulo("Evaluación Final").descripcion("Examen")
                .fechaLimite(fechaLimite).curso(curso).build();
        request = EvaluacionRequestDTO.builder()
                .titulo("Evaluación Final").descripcion("Examen")
                .fechaLimite(fechaLimite).idCurso(1L).build();
    }

    @Test
    void crear_conDatosValidos_guardaYDevuelveEvaluacion() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        EvaluacionResponseDTO respuesta = evaluacionService.crear(request);

        assertEquals(10L, respuesta.getIdEvaluacion());
        assertEquals("Evaluación Final", respuesta.getTitulo());
        assertEquals(1L, respuesta.getIdCurso());
        assertEquals("Desarrollo Web", respuesta.getNombreCurso());
        assertEquals(fechaLimite, respuesta.getFechaLimite());
        verify(evaluacionRepository).save(any(Evaluacion.class));
    }

    @Test
    void crear_conCursoInexistente_lanzaNotFound() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluacionService.crear(request));
        verify(evaluacionRepository, never()).save(any());
    }

    @Test
    void crear_conCursoDesactivado_lanzaReglaDeNegocio() {
        curso.setEstado(false);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        assertThrows(BusinessRuleException.class, () -> evaluacionService.crear(request));
        verify(evaluacionRepository, never()).save(any());
    }

    @Test
    void crear_segundaEvaluacionDelMismoCurso_sePermite() {
        Evaluacion segunda = Evaluacion.builder()
                .idEvaluacion(11L).titulo("Evaluación Parcial").curso(curso).build();

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(evaluacionRepository.save(any(Evaluacion.class)))
                .thenReturn(evaluacion, segunda);

        EvaluacionResponseDTO primeraRespuesta = evaluacionService.crear(request);
        request.setTitulo("Evaluación Parcial");
        EvaluacionResponseDTO segundaRespuesta = evaluacionService.crear(request);

        assertEquals(10L, primeraRespuesta.getIdEvaluacion());
        assertEquals(11L, segundaRespuesta.getIdEvaluacion());
        verify(evaluacionRepository, times(2)).save(any(Evaluacion.class));
    }

    @Test
    void obtenerPorId_inexistente_lanzaNotFound() {
        when(evaluacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluacionService.obtenerPorId(99L));
    }

    @Test
    void listar_sinFiltro_devuelveTodas() {
        when(evaluacionRepository.buscar(null)).thenReturn(List.of(evaluacion));

        List<EvaluacionResponseDTO> lista = evaluacionService.listar(null);

        assertEquals(1, lista.size());
        assertEquals(fechaLimite, lista.get(0).getFechaLimite());
        verify(cursoRepository, never()).findById(any());
    }

    @Test
    void listar_conCursoInexistente_lanzaNotFound() {
        when(cursoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluacionService.listar(5L));
        verify(evaluacionRepository, never()).buscar(any());
    }

    @Test
    void actualizar_conDatosValidos_modificaEvaluacion() {
        LocalDateTime nuevaFecha = fechaLimite.plusDays(1);
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenAnswer(inv -> inv.getArgument(0));
        request.setTitulo("Nuevo título");
        request.setFechaLimite(nuevaFecha);

        EvaluacionResponseDTO respuesta = evaluacionService.actualizar(10L, request);

        assertEquals("Nuevo título", respuesta.getTitulo());
        assertEquals(nuevaFecha, respuesta.getFechaLimite());
        verify(evaluacionRepository).save(evaluacion);
    }

    @Test
    void validarDisponible_conEvaluacionVigente_devuelveEvaluacion() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));

        EvaluacionResponseDTO respuesta =
                evaluacionService.validarDisponible(10L, 4L, true);

        assertEquals(10L, respuesta.getIdEvaluacion());
        assertEquals("Evaluación Final", respuesta.getTitulo());
    }

    @Test
    void validarDisponible_conCursoDesactivado_lanzaReglaDeNegocio() {
        curso.setEstado(false);
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));

        assertThrows(
                BusinessRuleException.class,
                () -> evaluacionService.validarDisponible(10L, 4L, true)
        );
    }

    @Test
    void validarDisponible_sinInscripcion_lanzaReglaDeNegocio() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> evaluacionService.validarDisponible(10L, 4L, false)
        );

        assertEquals("El estudiante no está inscrito en el curso de la evaluación",
                excepcion.getMessage());
    }

    @Test
    void validarDisponible_conFechaLimiteVencida_lanzaReglaDeNegocio() {
        evaluacion.setFechaLimite(LocalDateTime.now().minusDays(1));
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));

        BusinessRuleException excepcion = assertThrows(
                BusinessRuleException.class,
                () -> evaluacionService.validarDisponible(10L, 4L, true)
        );

        assertEquals("La evaluación ya no está disponible: venció la fecha límite",
                excepcion.getMessage());
    }

    @Test
    void validarDisponible_sinFechaLimite_esValida() {
        evaluacion.setFechaLimite(null);
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));

        EvaluacionResponseDTO respuesta =
                evaluacionService.validarDisponible(10L, 4L, true);

        assertNull(respuesta.getFechaLimite());
    }

    @Test
    void eliminar_existente_laBorra() {
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));

        evaluacionService.eliminar(10L);

        verify(evaluacionRepository).delete(evaluacion);
    }

    @Test
    void eliminar_inexistente_lanzaNotFound() {
        when(evaluacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluacionService.eliminar(99L));
        verify(evaluacionRepository, never()).delete(any());
    }
}
