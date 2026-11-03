package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PetDTO {
    private Long id;

    @NotBlank(message = "Nome do pet é obrigatório")
    @Size(max = 60)
    private String nome;

    @NotBlank(message = "Espécie é obrigatória")
    @Size(max = 30)
    private String especie;

    @Size(max = 50)
    private String raca;

    private LocalDate dataNascimento;

    private Double peso;

    @NotNull(message = "ID do tutor é obrigatório")
    private Long tutorId;

    private String tutorNome;
}
