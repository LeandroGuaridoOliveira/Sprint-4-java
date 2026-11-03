package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoConsultaDTO {

    @NotNull(message = "O ID do agendamento é obrigatório.")
    private Long agendamentoId;

    @NotNull(message = "O ID do pet é obrigatório.")
    private Long petId;

    private Long veterinarioId;

    @NotNull(message = "O peso aferido do animal nesta consulta é obrigatório.")
    @DecimalMin(value = "0.1", message = "O peso deve ser maior que zero.")
    private Double pesoAtual;

    @NotBlank(message = "A anamnese e conduta clínica são obrigatórias.")
    @Size(min = 10, max = 500, message = "A conduta deve conter entre 10 e 500 caracteres.")
    private String condutaClinica;

    @NotBlank(message = "A prescrição ou desfecho clínico é obrigatório.")
    @Size(min = 5, max = 300, message = "A prescrição ou resultado deve ter entre 5 e 300 caracteres.")
    private String prescricaoOuResultado;

    @NotBlank(message = "Informe a classificação do procedimento realizado.")
    private String tipoProcedimento;

    private Boolean aplicarProtocoloPreventivo;

    private String observacoesTutor;
}
