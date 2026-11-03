package br.com.fiap.clyvovet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_CLYVO_VETERINARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Veterinario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_veterinario")
    private Long id;

    @Column(name = "nm_veterinario", nullable = false, length = 100)
    private String nome;

    @Column(name = "nr_crmv", nullable = false, unique = true, length = 20)
    private String crmv;

    @Column(name = "ds_especialidade", length = 80)
    private String especialidade;

    @Column(name = "nr_telefone", length = 15)
    private String telefone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_clinica", nullable = false)
    private Clinica clinica;
}
