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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del CRUD de Evaluación (CU10, CU17). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EvaluacionService {

    private final EvaluacionRepository evaluacionRepository;
    private final CursoRepository cursoRepository;

    /** Lista las evaluaciones; si se envía idCurso, solo las de ese curso. */
    public List<EvaluacionResponseDTO> listar(Long idCurso) {
        if (idCurso != null) {
            validarCurso(idCurso);
        }
        return evaluacionRepository.buscar(idCurso).stream()
                .map(this::toResponse)
                .toList();
    }

    public EvaluacionResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarEvaluacion(id));
    }

    @Transactional
    public EvaluacionResponseDTO crear(EvaluacionRequestDTO dto) {
        Curso curso = validarCurso(dto.getIdCurso());

        Evaluacion evaluacion = Evaluacion.builder()
                .titulo(dto.getTitulo().trim())
                .descripcion(dto.getDescripcion())
                .fechaLimite(dto.getFechaLimite())
                .curso(curso)
                .build();
        return toResponse(evaluacionRepository.save(evaluacion));
    }

    @Transactional
    public EvaluacionResponseDTO actualizar(Long id, EvaluacionRequestDTO dto) {
        Evaluacion evaluacion = buscarEvaluacion(id);
        Curso curso = validarCurso(dto.getIdCurso());

        evaluacion.setTitulo(dto.getTitulo().trim());
        evaluacion.setDescripcion(dto.getDescripcion());
        evaluacion.setFechaLimite(dto.getFechaLimite());
        evaluacion.setCurso(curso);
        return toResponse(evaluacionRepository.save(evaluacion));
    }

    @Transactional
    public void eliminar(Long id) {
        evaluacionRepository.delete(buscarEvaluacion(id));
    }

    /** CU10: verifica que la evaluación esté disponible para el estudiante. */
    @Transactional(readOnly = true)
    public EvaluacionResponseDTO validarDisponible(Long idEvaluacion, Long idUsuario,
                                                    boolean inscrito) {
        Evaluacion evaluacion = buscarEvaluacion(idEvaluacion);

        if (!Boolean.TRUE.equals(evaluacion.getCurso().getEstado())) {
            throw new BusinessRuleException("El curso de la evaluación está desactivado");
        }
        if (!inscrito) {
            throw new BusinessRuleException(
                    "El estudiante no está inscrito en el curso de la evaluación");
        }
        if (evaluacion.getFechaLimite() != null
                && evaluacion.getFechaLimite().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessRuleException("La evaluación ya no está disponible: venció la fecha límite");
        }
        return toResponse(evaluacion);
    }

    // ---------- métodos auxiliares ----------

    private Evaluacion buscarEvaluacion(Long id) {
        return evaluacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evaluación", id));
    }

    private Curso validarCurso(Long idCurso) {
        Curso curso = cursoRepository.findById(idCurso)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", idCurso));
        if (!Boolean.TRUE.equals(curso.getEstado())) {
            throw new BusinessRuleException("El curso con id " + idCurso + " está desactivado");
        }
        return curso;
    }

    private EvaluacionResponseDTO toResponse(Evaluacion evaluacion) {
        return EvaluacionResponseDTO.builder()
                .idEvaluacion(evaluacion.getIdEvaluacion())
                .titulo(evaluacion.getTitulo())
                .descripcion(evaluacion.getDescripcion())
                .fechaLimite(evaluacion.getFechaLimite())
                .idCurso(evaluacion.getCurso().getIdCurso())
                .nombreCurso(evaluacion.getCurso().getNombreCurso())
                .build();
    }
}
