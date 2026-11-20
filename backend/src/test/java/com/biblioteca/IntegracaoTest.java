package com.biblioteca;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração: sobe o Spring inteiro com H2 em memória (perfil "test").
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class IntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long criarAutor(String nome) throws Exception {
        String json = "{\"nome\":\"" + nome + "\",\"nacionalidade\":\"Brasileira\",\"dataNascimento\":\"1839-06-21\"}";
        String resposta = mockMvc.perform(post("/api/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(resposta);
        return node.get("id").asLong();
    }

    @Test
    @DisplayName("Deve vincular livro a autor")
    void deveVincularLivroAAutor() throws Exception {
        Long autorId = criarAutor("Machado de Assis");

        String livroJson = "{\"titulo\":\"Dom Casmurro\",\"isbn\":\"978-8535910667\","
                + "\"anoPublicacao\":1899,\"genero\":\"Romance\",\"autor\":{\"id\":" + autorId + "}}";

        String resposta = mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(livroJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.autor.id").value(autorId))
                .andReturn().getResponse().getContentAsString();

        Long livroId = objectMapper.readTree(resposta).get("id").asLong();

        // Busca o livro e confere que o autor veio completo (nome carregado do banco)
        mockMvc.perform(get("/api/livros/" + livroId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.autor.nome").value("Machado de Assis"));

        // (extra) endpoint de livros por autor
        mockMvc.perform(get("/api/livros/autor/" + autorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titulo").value("Dom Casmurro"));
    }

    @Test
    @DisplayName("(extra) Não deve excluir autor que possui livros: 409")
    void naoDeveExcluirAutorComLivros() throws Exception {
        Long autorId = criarAutor("José de Alencar");
        mockMvc.perform(post("/api/livros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Iracema\",\"autor\":{\"id\":" + autorId + "}}"))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/autores/" + autorId))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Deve executar fluxo completo do autor: criar, buscar, atualizar, deletar")
    void deveExecutarFluxoCompletoAutor() throws Exception {
        Long id = criarAutor("Clarice Lispector");

        mockMvc.perform(get("/api/autores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Clarice Lispector"));

        mockMvc.perform(put("/api/autores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Clarice Lispector\",\"nacionalidade\":\"Ucraniana-Brasileira\",\"dataNascimento\":\"1920-12-10\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nacionalidade").value("Ucraniana-Brasileira"));

        mockMvc.perform(delete("/api/autores/" + id))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/autores/" + id))
                .andExpect(status().isNotFound());
    }
}
