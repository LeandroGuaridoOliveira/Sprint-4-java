package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VeterinarioDTO {
    private Long id;

    @NotBlank(message = "Nome do veterinário é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "CRMV é obrigatório")
    @Size(max = 20)
    private String crmv;

    @Size(max = 80)
    private String especialidade;

    @Size(max = 15)
    private String telefone;

    @NotNull(message = "ID da clínica é obrigatório")
    private Long clinicaId;

    private String clinicaNome;
}
