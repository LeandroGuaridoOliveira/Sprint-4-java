package br.com.fiap.clyvovet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_CLYVO_TUTOR")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tutor")
    private Long id;

    @Column(name = "nm_tutor", nullable = false, length = 100)
    private String nome;

    @Column(name = "nr_cpf", nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(name = "ds_email", nullable = false, length = 100)
    private String email;

    @Column(name = "nr_telefone", nullable = false, length = 15)
    private String telefone;

    @Column(name = "ds_endereco", length = 200)
    private String endereco;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Pet> pets = new ArrayList<>();
}
