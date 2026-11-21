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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    void setUp() {
        curso = Curso.builder().idCurso(1L).nombreCurso("Desarrollo Web").build();
        evaluacion = Evaluacion.builder()
                .idEvaluacion(10L).titulo("Evaluación Final").descripcion("Examen").curso(curso).build();
        request = EvaluacionRequestDTO.builder()
                .titulo("Evaluación Final").descripcion("Examen").idCurso(1L).build();
    }

    @Test
    void crear_conDatosValidos_guardaYDevuelveEvaluacion() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(evaluacionRepository.existsByCurso_IdCurso(1L)).thenReturn(false);
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        EvaluacionResponseDTO respuesta = evaluacionService.crear(request);

        assertEquals(10L, respuesta.getIdEvaluacion());
        assertEquals("Evaluación Final", respuesta.getTitulo());
        assertEquals(1L, respuesta.getIdCurso());
        assertEquals("Desarrollo Web", respuesta.getNombreCurso());
        verify(evaluacionRepository).save(any(Evaluacion.class));
    }

    @Test
    void crear_conCursoInexistente_lanzaNotFound() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> evaluacionService.crear(request));
        verify(evaluacionRepository, never()).save(any());
    }

    @Test
    void crear_conCursoQueYaTieneEvaluacion_lanzaReglaDeNegocio() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(evaluacionRepository.existsByCurso_IdCurso(1L)).thenReturn(true);

        assertThrows(BusinessRuleException.class, () -> evaluacionService.crear(request));
        verify(evaluacionRepository, never()).save(any());
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
        when(evaluacionRepository.findById(10L)).thenReturn(Optional.of(evaluacion));
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(evaluacionRepository.existsByCurso_IdCursoAndIdEvaluacionNot(1L, 10L)).thenReturn(false);
        when(evaluacionRepository.save(any(Evaluacion.class))).thenAnswer(inv -> inv.getArgument(0));
        request.setTitulo("Nuevo título");

        EvaluacionResponseDTO respuesta = evaluacionService.actualizar(10L, request);

        assertEquals("Nuevo título", respuesta.getTitulo());
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
