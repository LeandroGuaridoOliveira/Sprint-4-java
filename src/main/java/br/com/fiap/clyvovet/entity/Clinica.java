package br.com.fiap.clyvovet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_CLYVO_CLINICA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Clinica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clinica")
    private Long id;

    @Column(name = "nm_clinica", nullable = false, length = 100)
    private String nome;

    @Column(name = "nr_cnpj", nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(name = "ds_endereco", nullable = false, length = 200)
    private String endereco;

    @Column(name = "nr_telefone", nullable = false, length = 15)
    private String telefone;

    @Column(name = "ds_email", length = 100)
    private String email;

    @OneToMany(mappedBy = "clinica", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Veterinario> veterinarios = new ArrayList<>();
}
