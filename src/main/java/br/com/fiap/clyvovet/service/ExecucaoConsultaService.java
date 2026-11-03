package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.AtendimentoConsultaDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.EventoSaude;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.EventoSaudeRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ExecucaoConsultaService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final EventoSaudeRepository eventoSaudeRepository;

    public ExecucaoConsultaService(AgendamentoRepository agendamentoRepository,
                                  PetRepository petRepository,
                                  VeterinarioRepository veterinarioRepository,
                                  EventoSaudeRepository eventoSaudeRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.eventoSaudeRepository = eventoSaudeRepository;
    }

    /**
     * Execução Clínica de Atendimento e Atualização de Prontuário.
     * Operação @Transactional abrangendo:
     * 1. Atualização do Peso do Pet
     * 2. Criação do EventoSaude (histórico médico oficial)
     * 3. Aplicação/Reforço de protocolo preventivo se solicitado
     * 4. Encerramento do Agendamento para status CONCLUIDO
     */
    @Transactional
    public EventoSaude executarAtendimentoClinico(AtendimentoConsultaDTO dto) {
        // 1. Localiza e valida o Agendamento
        Agendamento agendamento = agendamentoRepository.findById(dto.getAgendamentoId())
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento não encontrado: " + dto.getAgendamentoId()));

        if ("CONCLUIDO".equalsIgnoreCase(agendamento.getStatus())) {
            throw new RegraDeNegocioException("Este agendamento já foi finalizado anteriormente.");
        }

        if ("CANCELADO".equalsIgnoreCase(agendamento.getStatus())) {
            throw new RegraDeNegocioException("Não é possível realizar atendimento em uma consulta que foi cancelada.");
        }

        // 2. Localiza e valida o Pet
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado: " + dto.getPetId()));

        // 3. Obtém o Veterinário responsável (da consulta ou passado no DTO)
        Veterinario vet = agendamento.getVeterinario();
        if (dto.getVeterinarioId() != null) {
            vet = veterinarioRepository.findById(dto.getVeterinarioId()).orElse(vet);
        }

        // 4. PASSO A: Atualização clínica do Pet (aferição de peso mais recente)
        pet.setPeso(dto.getPesoAtual());
        petRepository.save(pet);

        // 5. PASSO B: Criação do histórico médico oficial do animal (EventoSaude)
        String descricaoCompleta = dto.getCondutaClinica();
        if (dto.getObservacoesTutor() != null && !dto.getObservacoesTutor().isBlank()) {
            descricaoCompleta += " [Orientações ao Tutor: " + dto.getObservacoesTutor().trim() + "]";
        }

        String resultadoFinal = dto.getPrescricaoOuResultado();
        if (Boolean.TRUE.equals(dto.getAplicarProtocoloPreventivo())) {
            resultadoFinal += " (Protocolo Preventivo / Vacinal atualizado e validado)";
        }

        EventoSaude evento = EventoSaude.builder()
                .pet(pet)
                .veterinario(vet)
                .tipoEvento(dto.getTipoProcedimento())
                .dataEvento(LocalDate.now())
                .descricao(descricaoCompleta)
                .resultado(resultadoFinal)
                .build();

        EventoSaude eventoSalvo = eventoSaudeRepository.save(evento);

        // 6. PASSO C: Atualização do Agendamento para status CONCLUIDO
        agendamento.setStatus("CONCLUIDO");
        String registroFim = "Atendimento finalizado em " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + 
                " por " + vet.getNome();
        agendamento.setObservacao(agendamento.getObservacao() != null ? 
                agendamento.getObservacao() + " | " + registroFim : registroFim);
        agendamentoRepository.save(agendamento);

        return eventoSalvo;
    }
}
