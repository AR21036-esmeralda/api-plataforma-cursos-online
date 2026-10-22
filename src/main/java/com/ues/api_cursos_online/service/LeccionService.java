package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.LeccionRequestDTO;
import com.ues.api_cursos_online.dto.LeccionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.LeccionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del CRUD de Lección. */
@Service
@RequiredArgsConstructor
public class LeccionService {

    private final LeccionRepository leccionRepository;
    private final CursoRepository cursoRepository;

    @Transactional(readOnly = true)
    public List<LeccionResponseDTO> listar(Long idCurso) {
        List<Leccion> lecciones = idCurso == null
                ? leccionRepository.findAll().stream()
                    .sorted(java.util.Comparator.comparing(Leccion::getIdLeccion)).toList()
                : leccionRepository.findByCurso_IdCursoOrderByOrdenAsc(validarCurso(idCurso).getIdCurso());
        return lecciones.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public LeccionResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarLeccion(id));
    }

    @Transactional
    public LeccionResponseDTO crear(LeccionRequestDTO dto) {
        Curso curso = validarCurso(dto.getIdCurso());
        validarOrdenUnico(curso.getIdCurso(), dto.getOrden(), null);

        Leccion leccion = Leccion.builder()
                .titulo(dto.getTitulo().trim())
                .contenido(dto.getContenido())
                .orden(dto.getOrden())
                .curso(curso)
                .build();
        try {
            return toResponse(leccionRepository.save(leccion));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo crear la lección. Verifique que el orden no esté repetido en el curso.");
        }
    }

    @Transactional
    public LeccionResponseDTO actualizar(Long id, LeccionRequestDTO dto) {
        Leccion leccion = buscarLeccion(id);
        Curso curso = validarCurso(dto.getIdCurso());
        validarOrdenUnico(curso.getIdCurso(), dto.getOrden(), id);

        leccion.setTitulo(dto.getTitulo().trim());
        leccion.setContenido(dto.getContenido());
        leccion.setOrden(dto.getOrden());
        leccion.setCurso(curso);
        try {
            return toResponse(leccionRepository.save(leccion));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo actualizar la lección. Verifique que el orden no esté repetido en el curso.");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        leccionRepository.delete(buscarLeccion(id));
    }

    private Leccion buscarLeccion(Long id) {
        return leccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lección", id));
    }

    private Curso validarCurso(Long idCurso) {
        return cursoRepository.findById(idCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", idCurso));
    }

    private void validarOrdenUnico(Long idCurso, Integer orden, Long idLeccionActual) {
        boolean duplicado = idLeccionActual == null
                ? leccionRepository.existsByCurso_IdCursoAndOrden(idCurso, orden)
                : leccionRepository.existsByCurso_IdCursoAndOrdenAndIdLeccionNot(idCurso, orden, idLeccionActual);
        if (duplicado) {
            throw new BusinessRuleException("Ya existe una lección con ese orden en el curso indicado.");
        }
    }

    private LeccionResponseDTO toResponse(Leccion leccion) {
        return LeccionResponseDTO.builder()
                .idLeccion(leccion.getIdLeccion())
                .titulo(leccion.getTitulo())
                .contenido(leccion.getContenido())
                .orden(leccion.getOrden())
                .idCurso(leccion.getCurso().getIdCurso())
                .nombreCurso(leccion.getCurso().getNombreCurso())
                .build();
    }
}
