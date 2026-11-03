package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventoSaudeDTO {
    private Long id;

    @NotBlank(message = "Tipo do evento é obrigatório")
    @Size(max = 30)
    private String tipoEvento;

    @NotNull(message = "Data do evento é obrigatória")
    private LocalDate dataEvento;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500)
    private String descricao;

    @Size(max = 300)
    private String resultado;

    @NotNull(message = "ID do pet é obrigatório")
    private Long petId;

    @NotNull(message = "ID do veterinário é obrigatório")
    private Long veterinarioId;

    private String petNome;
    private String veterinarioNome;
}
