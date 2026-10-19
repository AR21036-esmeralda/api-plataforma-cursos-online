package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.RolRequestDTO;
import com.ues.api_cursos_online.dto.RolResponseDTO;
import com.ues.api_cursos_online.service.RolService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public List<RolResponseDTO> listar() {
        return rolService.listar();
    }

    @PostMapping
    public RolResponseDTO crear(@Valid @RequestBody RolRequestDTO dto) {
        return rolService.crear(dto);
    }

    @PutMapping("/{id}")
    public RolResponseDTO actualizar(@PathVariable Long id,
                                     @Valid @RequestBody RolRequestDTO dto) {
        return rolService.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
    }
}
