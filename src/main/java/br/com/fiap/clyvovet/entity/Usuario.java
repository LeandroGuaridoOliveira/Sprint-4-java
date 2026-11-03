package br.com.fiap.clyvovet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_CLYVO_USUARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nm_usuario", nullable = false, length = 100)
    private String nome;

    @Column(name = "ds_email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "ds_senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "ds_role", nullable = false, length = 30)
    private String role; // "ROLE_VET" ou "ROLE_TUTOR"

    @Column(name = "st_ativo", nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    @Column(name = "id_tutor")
    private Long tutorId;

    @Column(name = "id_veterinario")
    private Long veterinarioId;
}
