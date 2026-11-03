package br.com.fiap.clyvovet.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "T_CLYVO_EVENTO_SAUDE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventoSaude {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long id;

    @Column(name = "ds_tipo_evento", nullable = false, length = 30)
    private String tipoEvento;

    @Column(name = "dt_evento", nullable = false)
    private LocalDate dataEvento;

    @Column(name = "ds_descricao", nullable = false, length = 500)
    private String descricao;

    @Column(name = "ds_resultado", length = 300)
    private String resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pet", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_veterinario", nullable = false)
    private Veterinario veterinario;
}
