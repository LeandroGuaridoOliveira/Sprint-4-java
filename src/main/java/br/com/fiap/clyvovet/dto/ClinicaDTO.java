package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClinicaDTO {
    private Long id;

    @NotBlank(message = "Nome da clínica é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Size(max = 18)
    private String cnpj;

    @NotBlank(message = "Endereço é obrigatório")
    @Size(max = 200)
    private String endereco;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 15)
    private String telefone;

    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;
}
