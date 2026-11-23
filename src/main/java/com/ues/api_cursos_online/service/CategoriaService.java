package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.CategoriaRequestDTO;
import com.ues.api_cursos_online.dto.CategoriaResponseDTO;
import com.ues.api_cursos_online.entity.Categoria;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CategoriaRepository;
import com.ues.api_cursos_online.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio del CRUD de Categoría (CU23). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CursoRepository cursoRepository;

    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoriaResponseDTO obtenerPorId(Long id) {
        return toResponse(buscarCategoria(id));
    }

    @Transactional
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        Categoria categoria = new Categoria();
        aplicarDatos(categoria, dto);
        categoria.setEstado(dto.getEstado() != null ? dto.getEstado() : Boolean.TRUE);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarCategoria(id);
        aplicarDatos(categoria, dto);
        if (dto.getEstado() != null) {
            categoria.setEstado(dto.getEstado());
        }
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO desactivar(Long id) {
        return cambiarEstado(id, false);
    }

    @Transactional
    public CategoriaResponseDTO reactivar(Long id) {
        return cambiarEstado(id, true);
    }

    private CategoriaResponseDTO cambiarEstado(Long id, boolean estado) {
        Categoria categoria = buscarCategoria(id);
        categoria.setEstado(estado);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void eliminar(Long id) {
        Categoria categoria = buscarCategoria(id);
        if (cursoRepository.existsByCategoria_IdCategoria(id)) {
            throw new BusinessRuleException(
                    "No se puede eliminar la categoría porque tiene cursos asociados");
        }
        categoriaRepository.delete(categoria);
    }

    private Categoria buscarCategoria(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría", id));
    }

    private void aplicarDatos(Categoria categoria, CategoriaRequestDTO dto) {
        categoria.setNombreCategoria(dto.getNombreCategoria().trim());
        categoria.setDescripcion(dto.getDescripcion());
    }

    private CategoriaResponseDTO toResponse(Categoria categoria) {
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombreCategoria(categoria.getNombreCategoria())
                .descripcion(categoria.getDescripcion())
                .estado(categoria.getEstado())
                .build();
    }
}
