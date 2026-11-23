package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.dto.CategoriaRequestDTO;
import com.ues.api_cursos_online.dto.CategoriaResponseDTO;
import com.ues.api_cursos_online.service.CategoriaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categorías", description = "CU23 - Gestión de categorías de cursos")
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public List<CategoriaResponseDTO> listar() {
        return categoriaService.listar();
    }

    @GetMapping("/{id}")
    public CategoriaResponseDTO obtenerPorId(@PathVariable @Positive Long id) {
        return categoriaService.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponseDTO crear(@Valid @RequestBody CategoriaRequestDTO dto) {
        return categoriaService.crear(dto);
    }

    @PutMapping("/{id}")
    public CategoriaResponseDTO actualizar(@PathVariable @Positive Long id,
                                           @Valid @RequestBody CategoriaRequestDTO dto) {
        return categoriaService.actualizar(id, dto);
    }

    @PatchMapping("/{id}/desactivar")
    public CategoriaResponseDTO desactivar(@PathVariable @Positive Long id) {
        return categoriaService.desactivar(id);
    }

    @PatchMapping("/{id}/reactivar")
    public CategoriaResponseDTO reactivar(@PathVariable @Positive Long id) {
        return categoriaService.reactivar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        categoriaService.eliminar(id);
    }
}
