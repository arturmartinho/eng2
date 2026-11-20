package com.biblioteca.controller;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livros")
@CrossOrigin(origins = "*")
public class LivroController {

    private final LivroRepository livroRepository;

    public LivroController(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    @PostMapping
    public ResponseEntity<Livro> criar(@Valid @RequestBody Livro livro) {
        Livro salvo = livroRepository.save(livro);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Livro>> listar() {
        return ResponseEntity.ok(livroRepository.findAllByOrderByTituloAsc());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livro> buscar(@PathVariable Long id) {
        return livroRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Livro> atualizar(@PathVariable Long id, @Valid @RequestBody Livro dados) {
        return livroRepository.findById(id)
                .map(livro -> {
                    livro.setTitulo(dados.getTitulo());
                    livro.setIsbn(dados.getIsbn());
                    livro.setAnoPublicacao(dados.getAnoPublicacao());
                    livro.setGenero(dados.getGenero());
                    livro.setAutor(dados.getAutor()); // atualiza também o autor
                    return ResponseEntity.ok(livroRepository.save(livro));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!livroRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        livroRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // (extra) livros de um autor
    @GetMapping("/autor/{autorId}")
    public ResponseEntity<List<Livro>> listarPorAutor(@PathVariable Long autorId) {
        return ResponseEntity.ok(livroRepository.findByAutorId(autorId));
    }
}
