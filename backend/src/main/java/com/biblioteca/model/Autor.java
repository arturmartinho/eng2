package com.biblioteca.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Autor (lado "1" do relacionamento). Modelo: Contato.java da Agenda.
 * Não há List<Livro> aqui: o relacionamento fica só em Livro (evita loop no JSON).
 */
@Entity
@Table(name = "autores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 50)
    @Column(length = 50)
    private String nacionalidade;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
    }
}
