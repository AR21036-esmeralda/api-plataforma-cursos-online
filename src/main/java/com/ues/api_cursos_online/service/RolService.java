package com.ues.api_cursos_online.service;

import com.ues.api_cursos_online.dto.RolRequestDTO;
import com.ues.api_cursos_online.dto.RolResponseDTO;
import com.ues.api_cursos_online.entity.Rol;
import com.ues.api_cursos_online.exception.ResourceNotFoundException;
import com.ues.api_cursos_online.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<RolResponseDTO> listar() {
        return rolRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public RolResponseDTO crear(RolRequestDTO dto) {
        Rol rol = new Rol();
        rol.setNombreRol(dto.getNombreRol());
        rolRepository.save(rol);
        return toDTO(rol);
    }

    public RolResponseDTO actualizar(Long id, RolRequestDTO dto) {
        Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));

        rol.setNombreRol(dto.getNombreRol());
        rolRepository.save(rol);

        return toDTO(rol);
    }

    public void eliminar(Long id) {
        rolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol", id));

        rolRepository.deleteById(id);
    }

    private RolResponseDTO toDTO(Rol rol) {
        RolResponseDTO dto = new RolResponseDTO();
        dto.setIdRol(rol.getIdRol());
        dto.setNombreRol(rol.getNombreRol());
        return dto;
    }
}
