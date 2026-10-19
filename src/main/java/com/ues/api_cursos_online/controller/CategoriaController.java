package com.ues.api_cursos_online.controller;

import com.ues.api_cursos_online.entity.Categoria;
import com.ues.api_cursos_online.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> listar() {
        return categoriaRepository.findAll();
    }
    @PostMapping
    public Categoria crear(@RequestBody Categoria categoria) {
        return categoriaRepository.save(categoria);
    }
    @PutMapping("/{id}")
    public Categoria actualizar(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        existente.setNombreCategoria(categoria.getNombreCategoria());
        existente.setDescripcion(categoria.getDescripcion());
        existente.setEstado(categoria.getEstado());

        return categoriaRepository.save(existente);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        categoriaRepository.deleteById(id);
    }
}
