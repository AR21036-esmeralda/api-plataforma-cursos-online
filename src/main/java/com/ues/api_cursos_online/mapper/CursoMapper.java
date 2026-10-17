package com.ues.api_cursos_online.mapper;

import com.ues.api_cursos_online.dto.CursoResponseDTO;
import com.ues.api_cursos_online.entity.Curso;
import org.springframework.stereotype.Component;

/** Convierte entre la entidad Curso y sus DTO. */
@Component
public class CursoMapper {

    public CursoResponseDTO toResponse(Curso curso) {
        return CursoResponseDTO.builder()
                .idCurso(curso.getIdCurso())
                .nombreCurso(curso.getNombreCurso())
                .descripcion(curso.getDescripcion())
                .estado(curso.getEstado())
                .idCategoria(curso.getCategoria().getIdCategoria())
                .nombreCategoria(curso.getCategoria().getNombreCategoria())
                .idProfesor(curso.getProfesor().getIdUsuario())
                .nombreProfesor(curso.getProfesor().getNombreUsuario() + " "
                        + curso.getProfesor().getApellidoUsuario())
                .build();
    }
}
