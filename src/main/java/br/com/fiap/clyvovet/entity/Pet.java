package br.com.fiap.clyvovet.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "T_CLYVO_PET")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private Long id;

    @Column(name = "nm_pet", nullable = false, length = 60)
    private String nome;

    @Column(name = "ds_especie", nullable = false, length = 30)
    private String especie;

    @Column(name = "ds_raca", length = 50)
    private String raca;

    @Column(name = "dt_nascimento")
    private LocalDate dataNascimento;

    @Column(name = "nr_peso")
    private Double peso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tutor", nullable = false)
    private Tutor tutor;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Agendamento> agendamentos = new ArrayList<>();

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<EventoSaude> eventosSaude = new ArrayList<>();
}
