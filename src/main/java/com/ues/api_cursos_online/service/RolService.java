package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.RolRequestDTO;
import com.ues.api_cursos_online.dto.RolResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.exception.BusinessRuleException;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RolService {

    private static final Set<String> ROLES_PERMITIDOS =
            Set.of("ADMINISTRADOR", "ESTUDIANTE", "PROFESOR");

    private final RolRepository rolRepository;

    public List<RolResponseDTO> listar() {
        return rolRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public RolResponseDTO obtenerPorId(Long id) {
        return toDTO(buscarRol(id));
    }

    @Transactional
    public RolResponseDTO crear(RolRequestDTO dto) {
        String nombre = validarNombre(dto.getNombreRol());

        if (rolRepository.existsByNombreRol(nombre)) {
            throw new BusinessRuleException("El rol " + nombre + " ya existe");
        }

        Rol rol = new Rol();
        rol.setNombreRol(nombre);
        return toDTO(rolRepository.save(rol));
    }

    @Transactional
    public RolResponseDTO actualizar(Long id, RolRequestDTO dto) {
        Rol rol = buscarRol(id);
        String nombre = validarNombre(dto.getNombreRol());

        rolRepository.findByNombreRol(nombre)
                .filter(otro -> !otro.getIdRol().equals(id))
                .ifPresent(otro -> {
                    throw new BusinessRuleException("El rol " + nombre + " ya existe");
                });

        rol.setNombreRol(nombre);
        return toDTO(rolRepository.save(rol));
    }

    @Transactional
    public void eliminar(Long id) {
        buscarRol(id);
        rolRepository.deleteById(id);
    }

    private String validarNombre(String nombreRol) {
        if (nombreRol == null || nombreRol.isBlank()) {
            throw new BusinessRuleException("El nombre del rol es obligatorio");
        }

        String nombre = nombreRol.trim().toUpperCase(Locale.ROOT);
        if (!ROLES_PERMITIDOS.contains(nombre)) {
            throw new BusinessRuleException(
                    "El rol debe ser ADMINISTRADOR, ESTUDIANTE o PROFESOR");
        }
        return nombre;
    }

    private Rol buscarRol(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));
    }

    private RolResponseDTO toDTO(Rol rol) {
        RolResponseDTO dto = new RolResponseDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombreRol(rol.getNombreRol());
        return dto;
    }
}
