package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LivroController.class)
class LivroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LivroRepository livroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Autor autor() {
        Autor a = new Autor();
        a.setId(1L);
        a.setNome("Machado de Assis");
        return a;
    }

    private Livro novoLivro(Long id, String titulo) {
        Livro l = new Livro();
        l.setId(id);
        l.setTitulo(titulo);
        l.setIsbn("978-8535910667");
        l.setAnoPublicacao(1899);
        l.setGenero("Romance");
        l.setAutor(autor());
        return l;
    }

    @Test
    @DisplayName("Deve criar livro vinculado a um autor (201)")
    void deveCriarLivro() throws Exception {
        Livro entrada = novoLivro(null, "Dom Casmurro");
        Livro salvo = novoLivro(10L, "Dom Casmurro");
        when(livroRepository.save(any(Livro.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$.autor.id").value(1));
    }

    @Test
    @DisplayName("Deve retornar 400 quando o livro não tiver autor")
    void deveRejeitarLivroSemAutor() throws Exception {
        Livro entrada = novoLivro(null, "Sem Autor");
        entrada.setAutor(null);

        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 404 para id inexistente")
    void deveRetornar404ParaIdInexistente() throws Exception {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/livros/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar 404 ao atualizar id inexistente")
    void deveRetornar404AoAtualizarInexistente() throws Exception {
        when(livroRepository.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/livros/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoLivro(null, "X"))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve listar livros ordenados por título")
    void deveListarLivrosOrdenados() throws Exception {
        when(livroRepository.findAllByOrderByTituloAsc()).thenReturn(List.of(
                novoLivro(2L, "Dom Casmurro"),
                novoLivro(1L, "Memórias Póstumas de Brás Cubas")));

        mockMvc.perform(get("/api/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"))
                .andExpect(jsonPath("$[0].autor.nome").value("Machado de Assis"));
    }
}
