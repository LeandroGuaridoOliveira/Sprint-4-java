package br.com.fiap.clyvovet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "T_CLYVO_AGENDAMENTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agendamento")
    private Long id;

    @Column(name = "dt_agendamento", nullable = false)
    private LocalDateTime dataAgendamento;

    @Column(name = "ds_tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "ds_status", nullable = false, length = 20)
    private String status;

    @Column(name = "ds_observacao", length = 300)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pet", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    private Veterinario veterinario;
}
