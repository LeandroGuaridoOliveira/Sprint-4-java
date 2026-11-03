package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProtocoloDTO {
    private Long id;

    @NotBlank(message = "Espécie é obrigatória")
    @Size(max = 30)
    private String especie;

    @Size(max = 50)
    private String raca;

    @NotBlank(message = "Tipo do protocolo é obrigatório")
    @Size(max = 30)
    private String tipoProtocolo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500)
    private String descricao;

    @NotNull(message = "Intervalo em dias é obrigatório")
    private Integer intervaloDias;

    @Size(max = 30)
    private String faixaEtaria;
}
