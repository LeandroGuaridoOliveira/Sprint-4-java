package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TutorDTO {
    private Long id;

    @NotBlank(message = "Nome do tutor é obrigatório")
    @Size(max = 100)
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 14)
    private String cpf;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 15)
    private String telefone;

    @Size(max = 200)
    private String endereco;
}
