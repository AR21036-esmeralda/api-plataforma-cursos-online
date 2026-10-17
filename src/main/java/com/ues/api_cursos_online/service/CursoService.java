package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CursoRequestDTO;
import com.ues.api_cursos_online.dto.CursoResponseDTO;
import com.ues.api_cursos_online.entity.Categoria;
import com.ues.api_cursos_online.entity.Curso;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.mapper.CursoMapper;
import com.ues.api_cursos_online.repository.CategoriaRepository;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del CRUD de Curso. */
@Service
@RequiredArgsConstructor
public class CursoService {

    private static final String ROL_PROFESOR = "PROFESOR";

    private final CursoRepository cursoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoMapper cursoMapper;

    /** Lista los cursos; los filtros son opcionales (null = sin filtrar). */
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> listar(Boolean estado, Long idCategoria, Long idProfesor) {
        return cursoRepository.buscar(estado, idCategoria, idProfesor).stream()
                .map(cursoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CursoResponseDTO obtenerPorId(Long id) {
        return cursoMapper.toResponse(buscarCurso(id));
    }

    @Transactional
    public CursoResponseDTO crear(CursoRequestDTO dto) {
        Categoria categoria = obtenerCategoriaActiva(dto.getIdCategoria());
        Usuario profesor = obtenerProfesorActivo(dto.getIdProfesor());

        Curso curso = Curso.builder()
                .nombreCurso(dto.getNombreCurso().trim())
                .descripcion(dto.getDescripcion())
                .estado(dto.getEstado() != null ? dto.getEstado() : Boolean.TRUE)
                .categoria(categoria)
                .profesor(profesor)
                .build();

        return cursoMapper.toResponse(cursoRepository.save(curso));
    }

    @Transactional
    public CursoResponseDTO actualizar(Long id, CursoRequestDTO dto) {
        Curso curso = buscarCurso(id);

        // Solo se exige categoría/profesor activos si se están cambiando,
        // así un curso existente se puede editar aunque su categoría ya esté desactivada.
        if (!curso.getCategoria().getIdCategoria().equals(dto.getIdCategoria())) {
            curso.setCategoria(obtenerCategoriaActiva(dto.getIdCategoria()));
        }
        if (!curso.getProfesor().getIdUsuario().equals(dto.getIdProfesor())) {
            curso.setProfesor(obtenerProfesorActivo(dto.getIdProfesor()));
        }

        curso.setNombreCurso(dto.getNombreCurso().trim());
        curso.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null) {
            curso.setEstado(dto.getEstado());
        }

        return cursoMapper.toResponse(cursoRepository.save(curso));
    }

    @Transactional
    public void eliminar(Long id) {
        // Las lecciones, evaluaciones e inscripciones se eliminan en cascada (ON DELETE CASCADE)
        cursoRepository.delete(buscarCurso(id));
    }

    // ---------- métodos auxiliares ----------

    private Curso buscarCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
    }

    private Categoria obtenerCategoriaActiva(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", idCategoria));
        if (!Boolean.TRUE.equals(categoria.getEstado())) {
            throw new BusinessRuleException("La categoría con id " + idCategoria + " está desactivada");
        }
        return categoria;
    }

    private Usuario obtenerProfesorActivo(Long idProfesor) {
        Usuario usuario = usuarioRepository.findById(idProfesor)
                .orElseThrow(() -> new ResourceNotFoundException("Profesor", idProfesor));
        if (!ROL_PROFESOR.equals(usuario.getRol().getNombreRol())) {
            throw new BusinessRuleException("El usuario con id " + idProfesor + " no tiene rol PROFESOR");
        }
        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new BusinessRuleException("El profesor con id " + idProfesor + " está desactivado");
        }
        return usuario;
    }
}
