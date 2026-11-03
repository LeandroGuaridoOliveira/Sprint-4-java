package br.com.fiap.clyvovet.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_CLYVO_PROTOCOLO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Protocolo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_protocolo")
    private Long id;

    @Column(name = "ds_especie", nullable = false, length = 30)
    private String especie;

    @Column(name = "ds_raca", length = 50)
    private String raca;

    @Column(name = "ds_tipo_protocolo", nullable = false, length = 30)
    private String tipoProtocolo;

    @Column(name = "ds_descricao", nullable = false, length = 500)
    private String descricao;

    @Column(name = "nr_intervalo_dias", nullable = false)
    private Integer intervaloDias;

    @Column(name = "ds_faixa_etaria", length = 30)
    private String faixaEtaria;
}
