package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgendamentoDTO {
    private Long id;

    @NotNull(message = "Data do agendamento é obrigatória")
    private LocalDateTime dataAgendamento;

    @NotBlank(message = "Tipo é obrigatório")
    @Size(max = 30)
    private String tipo;

    @NotBlank(message = "Status é obrigatório")
    @Size(max = 20)
    private String status;

    @Size(max = 300)
    private String observacao;

    @NotNull(message = "ID do pet é obrigatório")
    private Long petId;

    @NotNull(message = "ID do veterinário é obrigatório")
    private Long veterinarioId;

    private String petNome;
    private String veterinarioNome;
}
