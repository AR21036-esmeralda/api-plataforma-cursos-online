package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.LeccionRequestDTO;
import com.ues.api_cursos_online.dto.LeccionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.LeccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeccionServiceTest {

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private LeccionService leccionService;

    private Curso curso;

    @BeforeEach
    void preparar() {
        curso = Curso.builder()
                .idCurso(1L)
                .nombreCurso("Curso de prueba")
                .build();
    }

    @Test
    void crearLeccionCuandoDatosSonValidos() {
        LeccionRequestDTO dto = new LeccionRequestDTO();
        dto.setTitulo("  Introducción  ");
        dto.setContenido("Contenido de prueba");
        dto.setOrden(1);
        dto.setIdCurso(1L);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(leccionRepository.existsByCurso_IdCursoAndOrden(1L, 1))
                .thenReturn(false);

        when(leccionRepository.save(any(Leccion.class))).thenAnswer(invocacion -> {
            Leccion leccion = invocacion.getArgument(0);
            leccion.setIdLeccion(10L);
            return leccion;
        });

        LeccionResponseDTO respuesta = leccionService.crear(dto);

        assertEquals(10L, respuesta.getIdLeccion());
        assertEquals("Introducción", respuesta.getTitulo());
        assertEquals(1, respuesta.getOrden());
        assertEquals(1L, respuesta.getIdCurso());

        verify(leccionRepository).save(any(Leccion.class));
    }

    @Test
    void crearLeccionCuandoCursoNoExisteLanzaExcepcion() {
        LeccionRequestDTO dto = new LeccionRequestDTO();
        dto.setTitulo("Introducción");
        dto.setContenido("Contenido de prueba");
        dto.setOrden(1);
        dto.setIdCurso(9999L);

        when(cursoRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> leccionService.crear(dto)
        );

        verify(leccionRepository, never()).save(any(Leccion.class));
    }

    @Test
    void crearLeccionConOrdenDuplicadoLanzaExcepcion() {
        LeccionRequestDTO dto = new LeccionRequestDTO();
        dto.setTitulo("Introducción");
        dto.setContenido("Contenido de prueba");
        dto.setOrden(1);
        dto.setIdCurso(1L);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(leccionRepository.existsByCurso_IdCursoAndOrden(1L, 1))
                .thenReturn(true);

        assertThrows(
                BusinessRuleException.class,
                () -> leccionService.crear(dto)
        );

        verify(leccionRepository, never()).save(any(Leccion.class));
    }
}