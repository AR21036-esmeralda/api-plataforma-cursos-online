package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.EvaluacionRequestDTO;
import com.ues.api_cursos_online.dto.EvaluacionResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Evaluacion;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.EvaluacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del CRUD de Evaluación. */
@Service
@RequiredArgsConstructor
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final CursoRepository cursoRepository;

    /** Lista las evaluaciones; si se envía idCurso, solo las de ese curso. */
    @Transactional(readOnly = true)
    public List<EvaluacionResponseDTO> listar(Long idCurso) {
        if (idCurso != null) {
            validarCurso(idCurso);
        }
        return evaluacionRepository.buscar(idCurso).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvaluacionResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarEvaluacion(id));
    }

    @Transactional
    public EvaluacionResponseDTO crear(EvaluacionRequestDTO dto) {
        Curso curso = validarCurso(dto.getIdCurso());
        validarCursoSinEvaluacion(curso.getIdCurso(), null);

        Evaluacion evaluacion = Evaluacion.builder()
                .titulo(dto.getTitulo().trim())
                .descripcion(dto.getDescripcion())
                .curso(curso)
                .build();
        try {
            return toResponse(evaluacionRepository.save(evaluacion));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo crear la evaluación. Verifique que el curso no tenga ya una evaluación.");
        }
    }

    @Transactional
    public EvaluacionResponseDTO actualizar(Long id, EvaluacionRequestDTO dto) {
        Evaluacion evaluacion = buscarEvaluacion(id);
        Curso curso = validarCurso(dto.getIdCurso());
        validarCursoSinEvaluacion(curso.getIdCurso(), id);

        evaluacion.setTitulo(dto.getTitulo().trim());
        evaluacion.setDescripcion(dto.getDescripcion());
        evaluacion.setCurso(curso);
        try {
            return toResponse(evaluacionRepository.save(evaluacion));
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessRuleException("No se pudo actualizar la evaluación. Verifique que el curso no tenga ya una evaluación.");
        }
    }

    @Transactional
    public void eliminar(Long id) {
        // Las calificaciones de la evaluación se eliminan en cascada (ON DELETE CASCADE)
        evaluacionRepository.delete(buscarEvaluacion(id));
    }

    // ---------- métodos auxiliares ----------

    private Evaluacion buscarEvaluacion(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación", id));
    }

    private Curso validarCurso(Long idCurso) {
        return cursoRepository.findById(idCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", idCurso));
    }

    /** Un curso solo puede tener una evaluación. */
    private void validarCursoSinEvaluacion(Long idCurso, Long idEvaluacionActual) {
        boolean yaTiene = idEvaluacionActual == null
                ? evaluacionRepository.existsByCurso_IdCurso(idCurso)
                : evaluacionRepository.existsByCurso_IdCursoAndIdEvaluacionNot(idCurso, idEvaluacionActual);
        if (yaTiene) {
            throw new BusinessRuleException("El curso con id " + idCurso + " ya tiene una evaluación");
        }
    }

    private EvaluacionResponseDTO toResponse(Evaluacion evaluacion) {
        return EvaluacionResponseDTO.builder()
                .idEvaluacion(evaluacion.getIdEvaluacion())
                .titulo(evaluacion.getTitulo())
                .descripcion(evaluacion.getDescripcion())
                .idCurso(evaluacion.getCurso().getIdCurso())
                .nombreCurso(evaluacion.getCurso().getNombreCurso())
                .build();
    }
}
