package br.com.fiap.clyvovet.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoAgendamentoDTO {

    @NotNull(message = "Por favor, selecione o pet para o atendimento.")
    private Long petId;

    @NotNull(message = "Por favor, selecione o médico veterinário responsável.")
    private Long veterinarioId;

    @NotBlank(message = "Informe a modalidade/tipo de atendimento.")
    private String tipo;

    @NotNull(message = "A data e horário do atendimento são obrigatórios.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Future(message = "A data e horário do agendamento devem ser futuros.")
    private LocalDateTime dataHora;

    @NotBlank(message = "Selecione a classificação de triagem inicial.")
    private String tipoTriagem;

    @NotBlank(message = "A descrição dos sintomas ou motivo da consulta é obrigatória.")
    @Size(min = 5, max = 300, message = "O relato deve conter entre 5 e 300 caracteres.")
    private String queixaPrincipal;

    private Boolean jejumRecomendado;
}
