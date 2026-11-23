package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.ProgresoRequestDTO;
import com.ues.api_cursos_online.dto.ProgresoResponseDTO;
import com.ues.api_cursos_online.entity.Leccion;
import com.ues.api_cursos_online.entity.Progreso;
import com.ues.api_cursos_online.entity.Usuario;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.CursoRepository;
import com.ues.api_cursos_online.repository.LeccionRepository;
import com.ues.api_cursos_online.repository.ProgresoRepository;
import com.ues.api_cursos_online.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LeccionRepository leccionRepository;
    private final CursoRepository cursoRepository;

    /**
     * Lista todos los registros de progreso.
     */
    public List<ProgresoResponseDTO> listar() {

        return progresoRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca un progreso por ID.
     */
    public ProgresoResponseDTO buscarPorId(Long id) {

        Progreso progreso = buscarEntidad(id);

        return toDTO(progreso);
    }

    /**
     * Crea un nuevo registro de progreso.
     */
    @Transactional
    public ProgresoResponseDTO crear(ProgresoRequestDTO dto) {

        Usuario usuario = usuarioRepository
                .findById(dto.getIdUsuario())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario",
                                dto.getIdUsuario()
                        )
                );

        Leccion leccion = leccionRepository
                .findById(dto.getIdLeccion())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leccion",
                                dto.getIdLeccion()
                        )
                );

        /*
         * Evita que un usuario tenga dos registros
         * de progreso para la misma lección.
         */
        boolean existe =
                progresoRepository
                        .existsByUsuario_IdUsuarioAndLeccion_IdLeccion(
                                dto.getIdUsuario(),
                                dto.getIdLeccion()
                        );

        if (existe) {

            throw new IllegalArgumentException(
                    "El usuario ya tiene un registro de progreso para esta leccion"
            );
        }

        /*
         * Si estado es null, se considera false.
         */
        boolean completada =
                Boolean.TRUE.equals(dto.getEstado());

        Progreso progreso = Progreso.builder()
                .usuario(usuario)
                .leccion(leccion)
                .estado(completada)
                .fechaCompletada(
                        completada
                                ? LocalDateTime.now()
                                : null
                )
                .build();

        Progreso guardado =
                progresoRepository.save(progreso);

        return toDTO(guardado);
    }

    /**
     * Actualiza un registro de progreso.
     */
    @Transactional
    public ProgresoResponseDTO actualizar(
            Long id,
            ProgresoRequestDTO dto
    ) {

        Progreso progreso = buscarEntidad(id);

        Usuario usuario = usuarioRepository
                .findById(dto.getIdUsuario())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Usuario",
                                dto.getIdUsuario()
                        )
                );

        Leccion leccion = leccionRepository
                .findById(dto.getIdLeccion())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Leccion",
                                dto.getIdLeccion()
                        )
                );

        /*
         * Comprobamos si cambia el usuario o la lección.
         */
        boolean cambioUsuario =
                !progreso.getUsuario()
                        .getIdUsuario()
                        .equals(dto.getIdUsuario());

        boolean cambioLeccion =
                !progreso.getLeccion()
                        .getIdLeccion()
                        .equals(dto.getIdLeccion());

        /*
         * Si cambia la relación, comprobamos que
         * usuario + lección no exista previamente.
         */
        if (cambioUsuario || cambioLeccion) {

            boolean duplicado =
                    progresoRepository
                            .existsByUsuario_IdUsuarioAndLeccion_IdLeccion(
                                    dto.getIdUsuario(),
                                    dto.getIdLeccion()
                            );

            if (duplicado) {

                throw new IllegalArgumentException(
                        "El usuario ya tiene un registro de progreso para esta leccion"
                );
            }
        }

        boolean estabaCompletada =
                Boolean.TRUE.equals(
                        progreso.getEstado()
                );

        boolean completada =
                Boolean.TRUE.equals(
                        dto.getEstado()
                );

        progreso.setUsuario(usuario);
        progreso.setLeccion(leccion);
        progreso.setEstado(completada);

        /*
         * PENDIENTE -> COMPLETADA
         *
         * Registramos la fecha en la que
         * se completa la lección.
         */
        if (!estabaCompletada && completada) {

            progreso.setFechaCompletada(
                    LocalDateTime.now()
            );
        }

        /*
         * COMPLETADA -> PENDIENTE
         *
         * La fecha debe volver a NULL.
         */
        if (!completada) {

            progreso.setFechaCompletada(null);
        }

        Progreso actualizado =
                progresoRepository.save(progreso);

        return toDTO(actualizado);
    }

    /**
     * Elimina un registro de progreso.
     */
    @Transactional
    public void eliminar(Long id) {

        Progreso progreso = buscarEntidad(id);

        progresoRepository.delete(progreso);
    }

    /**
     * Obtiene todo el progreso de un estudiante.
     */
    public List<ProgresoResponseDTO> listarPorEstudiante(
            Long idUsuario
    ) {

        if (!usuarioRepository.existsById(idUsuario)) {

            throw new ResourceNotFoundException(
                    "Usuario",
                    idUsuario
            );
        }

        return progresoRepository
                .findByUsuario_IdUsuario(idUsuario)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Obtiene el progreso de un estudiante
     * dentro de un curso.
     */
    public List<ProgresoResponseDTO> listarPorEstudianteYCurso(
            Long idUsuario,
            Long idCurso
    ) {

        if (!usuarioRepository.existsById(idUsuario)) {

            throw new ResourceNotFoundException(
                    "Usuario",
                    idUsuario
            );
        }

        if (!cursoRepository.existsById(idCurso)) {

            throw new ResourceNotFoundException(
                    "Curso",
                    idCurso
            );
        }

        return progresoRepository
                .findByUsuario_IdUsuarioAndLeccion_Curso_IdCurso(
                        idUsuario,
                        idCurso
                )
                .stream()
                .map(this::toDTO)
                .toList();
    }

    /**
     * Busca el progreso de un usuario
     * en una lección concreta.
     */
    public ProgresoResponseDTO buscarPorUsuarioYLeccion(
            Long idUsuario,
            Long idLeccion
    ) {

        Progreso progreso =
                progresoRepository
                        .findByUsuario_IdUsuarioAndLeccion_IdLeccion(
                                idUsuario,
                                idLeccion
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Progreso",
                                        idLeccion
                                )
                        );

        return toDTO(progreso);
    }

    /**
     * Busca internamente una entidad Progreso.
     */
    private Progreso buscarEntidad(Long id) {

        return progresoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Progreso",
                                id
                        )
                );
    }

    /**
     * Convierte Progreso a ProgresoResponseDTO.
     */
    private ProgresoResponseDTO toDTO(
            Progreso progreso
    ) {

        ProgresoResponseDTO dto =
                new ProgresoResponseDTO();

        dto.setIdProgreso(
                progreso.getIdProgreso()
        );

        // Usuario
        dto.setIdUsuario(
                progreso.getUsuario()
                        .getIdUsuario()
        );

        dto.setNombreUsuario(
                progreso.getUsuario()
                        .getNombreUsuario()
        );

        dto.setApellidoUsuario(
                progreso.getUsuario()
                        .getApellidoUsuario()
        );

        // Lección
        dto.setIdLeccion(
                progreso.getLeccion()
                        .getIdLeccion()
        );

        dto.setTituloLeccion(
                progreso.getLeccion()
                        .getTitulo()
        );

        // Curso de la lección
        dto.setIdCurso(
                progreso.getLeccion()
                        .getCurso()
                        .getIdCurso()
        );

        dto.setNombreCurso(
                progreso.getLeccion()
                        .getCurso()
                        .getNombreCurso()
        );

        // Estado del progreso
        dto.setEstado(
                progreso.getEstado()
        );

        dto.setFechaCompletada(
                progreso.getFechaCompletada()
        );

        return dto;
    }
}