package br.com.fiap.clyvovet.service;

import br.com.fiap.clyvovet.dto.SolicitacaoAgendamentoDTO;
import br.com.fiap.clyvovet.entity.Agendamento;
import br.com.fiap.clyvovet.entity.Pet;
import br.com.fiap.clyvovet.entity.Veterinario;
import br.com.fiap.clyvovet.exception.RegraDeNegocioException;
import br.com.fiap.clyvovet.exception.ResourceNotFoundException;
import br.com.fiap.clyvovet.repository.AgendamentoRepository;
import br.com.fiap.clyvovet.repository.PetRepository;
import br.com.fiap.clyvovet.repository.VeterinarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Service
public class TriagemAgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;

    public TriagemAgendamentoService(AgendamentoRepository agendamentoRepository,
                                   PetRepository petRepository,
                                   VeterinarioRepository veterinarioRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
    }

    /**
     * Processamento de Triagem e Agendamento com Verificação de Conflito de Horário.
     * Implementa validações de horário de funcionamento e prevenção de sobreposição na agenda médica.
     */
    @Transactional
    public Agendamento processarTriagemEAgendamento(SolicitacaoAgendamentoDTO dto) {
        // 1. Validação de entidades existentes
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet não encontrado com ID: " + dto.getPetId()));

        Veterinario vet = veterinarioRepository.findById(dto.getVeterinarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Veterinário não encontrado com ID: " + dto.getVeterinarioId()));

        LocalDateTime dataHora = dto.getDataHora();

        // 2. Validação temporal e regras de clínica
        if (dataHora.isBefore(LocalDateTime.now())) {
            throw new RegraDeNegocioException("A data do agendamento deve ser uma data futura.");
        }

        if (dataHora.getDayOfWeek() == DayOfWeek.SUNDAY) {
            throw new RegraDeNegocioException("A clínica não realiza atendimentos eletivos aos domingos. Selecione um dia de segunda a sábado.");
        }

        int hora = dataHora.getHour();
        if (hora < 8 || hora >= 18) {
            throw new RegraDeNegocioException("O expediente clínico funciona exclusivamente das 08:00 às 18:00.");
        }

        if (dataHora.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new RegraDeNegocioException("Agendamentos exigem antecedência mínima de 1 hora para preparação da sala clínica.");
        }

        // 3. Regra de Conflito de Horário: Evita sobreposição na agenda do Veterinário (intervalo de 30 min)
        LocalDateTime inicioJanela = dataHora.minusMinutes(29);
        LocalDateTime fimJanela = dataHora.plusMinutes(29);

        boolean vetOcupado = agendamentoRepository.existsConflitoVeterinario(vet.getId(), inicioJanela, fimJanela);
        if (vetOcupado) {
            throw new RegraDeNegocioException("O(A) médico(a) " + vet.getNome() + 
                    " já possui atendimento marcado neste horário (" + dataHora.toLocalTime() + "). Escolha outro horário ou veterinário.");
        }

        // 4. Regra de Conflito do Pet: O animal não pode estar em dois lugares ao mesmo tempo
        boolean petOcupado = agendamentoRepository.existsConflitoPet(pet.getId(), inicioJanela, fimJanela);
        if (petOcupado) {
            throw new RegraDeNegocioException("O pet " + pet.getNome() + " já possui outro atendimento cadastrado nesse mesmo horário.");
        }

        // 5. Montagem do resumo da triagem na observação do prontuário
        StringBuilder obs = new StringBuilder();
        obs.append("[Triagem: ").append(dto.getTipoTriagem()).append("] ");
        obs.append("Queixa: ").append(dto.getQueixaPrincipal().trim());
        if (Boolean.TRUE.equals(dto.getJejumRecomendado())) {
            obs.append(" | Protocolo: Jejum prévio de 8h solicitado.");
        }

        // 6. Construção e persistência do agendamento com status CONFIRMADO
        Agendamento agendamento = Agendamento.builder()
                .pet(pet)
                .veterinario(vet)
                .tipo(dto.getTipo())
                .status("CONFIRMADO")
                .dataAgendamento(dataHora)
                .observacao(obs.toString())
                .build();

        return agendamentoRepository.save(agendamento);
    }
}
